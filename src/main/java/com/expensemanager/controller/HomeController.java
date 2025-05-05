package com.expensemanager.controller;

import com.expensemanager.database.ExpenseDAO;
import com.expensemanager.util.AlertUtils;
import com.expensemanager.util.DateUtil;
import com.expensemanager.module.Expense;
import com.expensemanager.util.SortUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.expensemanager.util.InputUtil.validateInput;
import static com.expensemanager.util.SortUtil.SortOption.*;

public class HomeController implements Initializable {
    public static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @FXML
    public Menu menuFile;
    @FXML
    public Menu menuView;
    @FXML
    public Menu menuStatistics;
    @FXML
    public TableView<Expense> expenseTableView;
    @FXML
    public TableColumn<Expense, String> amountColumn;
    @FXML
    public TableColumn<Expense, String> categoryColumn;
    @FXML
    public TableColumn<Expense, String> dateColumn;
    @FXML
    public TextField textFiel_Amount;
    @FXML
    public TextField textFiel_Category;
    public DatePicker DatePicker;

    //sắp xếp lần đầu mở ứng dụng
    private SortUtil.SortOption currentSortOption = DATE_DESC;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        steupTableColumn();
        loadExpenses(ExpenseDAO.getAllExpenses());
        setupDeleteExpenseKeyHandler();

    }

    public void steupTableColumn() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        dateColumn.setReorderable(false);
        amountColumn.setReorderable(false);
        categoryColumn.setReorderable(false);
    }

    private void loadExpenses(List<Expense> expenses) {
        try {
            ObservableList<Expense> expenseList = FXCollections.observableArrayList(expenses);

            SortUtil.sortExpenses(expenseList, currentSortOption);

            expenseTableView.setItems(expenseList);

            logger.info("Đã tải {} chi tiêu", expenseList.size());
        } catch (Exception e) {
            logger.error("Lỗi khi tải dữ liệu chi tiêu", e);
        }
    }

    public void handleAddNewExpense(ActionEvent actionEvent) {
        try {
            String amount = textFiel_Amount.getText().trim();
            String category = textFiel_Category.getText().trim();

            String date;
            Expense expense;

            if (DatePicker.getValue() != null) {
                date = DatePicker.getValue().toString();
            } else {
                date = LocalDate.now().toString();
            }

            if (!validateInput(amount, category, date)) {
                textFiel_Amount.clear();
                textFiel_Category.clear();
                DatePicker.setValue(null);
                return;
            }

            expense = new Expense(amount, category, date);
            ExpenseDAO.addExpense(expense);

            loadExpenses(ExpenseDAO.getAllExpenses());

            textFiel_Amount.clear();
            textFiel_Category.clear();
            DatePicker.setValue(null);

            logger.info("Thêm chi tiêu thành công: {}", expense);
        } catch (Exception e) {
            logger.error("Lỗi khi thêm chi tiêu", e);
            AlertUtils.showError("Lỗi", "Không thể thêm chi tiêu");
        }
    }

    @FXML
    private void handleImportFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file text chứa chi tiêu");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(expenseTableView.getScene().getWindow());

        if (selectedFile != null) {
            try {
                List<String> lines = Files.readAllLines(selectedFile.toPath());
                List<Expense> expenses = new ArrayList<>();
                int successCount = 0;
                int failCount = 0;

                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String amount = parts[0].trim();
                        String category = parts[1].trim();
                        String date = parts[2].trim();

                        if (validateInput(amount, category, date)) {
                            Expense expense = new Expense(amount, category, date);
                            ExpenseDAO.addExpense(expense);
                            expenses.add(expense);
                            successCount++;
                        } else {
                            failCount++;
                        }
                    } else {
                        failCount++;
                    }

                }
                ExpenseDAO.addExpenseList(expenses);
                loadExpenses(ExpenseDAO.getAllExpenses());
                AlertUtils.showAlert("Nhập từ file",
                        String.format("Đã nhập thành công %d chi tiêu\n" +
                                        "Không thể nhập %d chi tiêu",
                                successCount, failCount));
                logger.info("Đã nhập {} chi tiêu từ file, {} chi tiêu không hợp lệ",
                        successCount, failCount);
            } catch (IOException e) {
                logger.error("Lỗi khi đọc file: {}", e.getMessage());
                AlertUtils.showError("Lỗi", "Không thể đọc file");
            } catch (Exception e) {
                logger.error("Lỗi khi nhập chi tiêu từ file: {}", e.getMessage());
                AlertUtils.showError("Lỗi", "Không thể nhập chi tiêu từ file");
            }
        }
    }

    public void setupDeleteExpenseKeyHandler() {
        expenseTableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                Expense selectedItem = expenseTableView.getSelectionModel().getSelectedItem();

                if (selectedItem != null) {

                    try {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Xác nhận xóa");
                        alert.setHeaderText("Xóa chi tiêu đã chọn");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            ExpenseDAO.deleteExpenseById(selectedItem.getId());
                            loadExpenses(ExpenseDAO.getAllExpenses());
                            AlertUtils.showAlert("Thành công", "Đã xóa chi tiêu");
                        }
                    } catch (Exception e) {
                        logger.error("Lỗi khi xóa dữ liệu hiển thị", e);
                        AlertUtils.showError("Lỗi", "Không thể xóa dữ liệu hiển thị");
                    }
                }
            }
        });
    }

    public void deleteAllExpense(ActionEvent actionEvent) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Xóa tất cả dữ liệu hiển thị");
            alert.setContentText(
                    "Bạn có chắc chắn muốn xóa tất cả dữ liệu đang hiển thị? Hành động này không thể hoàn tác.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ExpenseDAO.deleteAllExpenses();

                loadExpenses(ExpenseDAO.getAllExpenses());

                logger.info("Đã xóa {} chi tiêu thành công", ExpenseDAO.countExpenses());
                AlertUtils.showAlert("Thành công", "Đã xóa " + ExpenseDAO.countExpenses() + " chi tiêu");
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xóa dữ liệu hiển thị", e);
            AlertUtils.showError("Lỗi", "Không thể xóa dữ liệu hiển thị");
        }
    }

    @FXML
    private void handleSortByDateDesc() {
        currentSortOption = DATE_DESC;
        loadExpenses(ExpenseDAO.getAllExpenses());
        logger.info("Đã sắp xếp theo thời gian (mới nhất trước)");
    }

    @FXML
    private void handleSortByDateAsc() {
        currentSortOption = DATE_ASC;
        loadExpenses(ExpenseDAO.getAllExpenses());
        logger.info("Đã sắp xếp theo thời gian (cũ nhất trước)");
    }

    @FXML
    private void handleSortByAmountDesc() {
        currentSortOption = AMOUNT_DESC;
        loadExpenses(ExpenseDAO.getAllExpenses());
        logger.info("Đã sắp xếp theo số tiền (cao nhất trước)");
    }

    @FXML
    private void handleSortByAmountAsc() {
        currentSortOption = AMOUNT_ASC;
        loadExpenses(ExpenseDAO.getAllExpenses());
        logger.info("Đã sắp xếp theo số tiền (thấp nhất trước)");
    }

    @FXML
    public void handleShowDailyChart(ActionEvent actionEvent) {
        openStatisticDailyWindow();

    }

    @FXML
    public void handleShowMonthlyChart(ActionEvent actionEvent) {
        openStatisticDateRangeWindow();
    }


    private void openStatisticDailyWindow() {
        StatisticsController tabPaneStatisticDaily = new StatisticsController();
        tabPaneStatisticDaily.startStatisticDaily(DateUtil.chooseDate("Chọn ngày thống kê"));
    }

    private void openStatisticDateRangeWindow() {
        StatisticsController tabPaneStatisticDateRange = new StatisticsController();
        tabPaneStatisticDateRange.startStatisticDateRange(
                DateUtil.chooseDate("Chọn ngày bắt đầu")
                , DateUtil.chooseDate("Chọn ngày kết thúc"));
    }

    public void handleViewByDate(ActionEvent actionEvent) {
        String date = DateUtil.chooseDate("Chọn ngày");
        loadExpenses(ExpenseDAO.getExpensesByDate(date));
    }

    public void handleViewByDateRange(ActionEvent actionEvent) {
        String dateStart = DateUtil.chooseDate("Chọn ngày bắt đầu");
        String dateEnd = DateUtil.chooseDate("Chọn ngày kết thúc");
        loadExpenses(ExpenseDAO.getExpensesByDateRange(dateStart, dateEnd));
    }

    public void handleViewByCategory(ActionEvent actionEvent) {
        TextField category = new TextField();
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Danh mục muốn xem");
        dialog.getDialogPane().setContent(category);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String categoryText = category.getText();
                loadExpenses(ExpenseDAO.getExpensesByCategory(categoryText));
                if (categoryText != null) {
                    return categoryText;
                } else {
                    return null;
                }
            }
            return null;
        });
        dialog.showAndWait();

    }

    public void handleViewAll(ActionEvent actionEvent) {
        loadExpenses(ExpenseDAO.getAllExpenses());
    }
}
