package com.expensemanager.controller;

import com.expensemanager.database.ExpenseDAO;
import com.expensemanager.module.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StatisticsController {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    public void startStatisticDaily(String date) {
        try {
            List<Expense> expenses = ExpenseDAO.getExpensesByDate(date);

            Stage stage = new Stage();
            TabPane tabPaneStatisticDaily = new TabPane();

            // Tạo Tab 1 - Tổng quan
            Tab tab1 = new Tab("Tổng quan");
            VBox labelsContainer = new VBox(10);
            labelsContainer.setPadding(new Insets(20));

            // Thêm các thành phần vào container
            labelsContainer.getChildren().addAll(
                createTitleLabel(date),
                createOverviewBoxByDate(date),
                createDetailTitle(),
                createCategoryDetails(date)
            );

            tab1.setContent(labelsContainer);

            // Tạo Tab 2 - Biểu đồ tròn
            Tab tab2 = new Tab("Biểu đồ tròn");
            VBox pieChartContainer = new VBox(10);
            pieChartContainer.setPadding(new Insets(20));

            PieChart pieChart = createPieChart(date, expenses);

            pieChartContainer.getChildren().add(pieChart);
            tab2.setContent(pieChartContainer);

            // Tạo Tab 3 - Biểu đồ cột
            Tab tab3 = new Tab("Biểu đồ cột");
            VBox barChartContainer = new VBox(10);
            barChartContainer.setPadding(new Insets(20));

            BarChart<String, Number> barChart = createBarChart(expenses);

            barChartContainer.getChildren().add(barChart);
            tab3.setContent(barChartContainer);

            // Thêm các tab vào TabPane
            tabPaneStatisticDaily.getTabs().addAll(tab1, tab2, tab3);

            // Tạo Scene và hiển thị
            Scene scene = new Scene(tabPaneStatisticDaily, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Thống kê chi tiêu ngày " + date);
            stage.show();

        } catch (Exception e) {
            logger.error("Lỗi khi hiển thị thống kê", e);
        }
    }

    public void startStatisticDateRange(String dateStart, String dateEnd) {
        try {
            List<Expense> expenses = ExpenseDAO.getExpensesByDateRange(dateStart,dateEnd);

            Stage stage = new Stage();
            TabPane tabPaneStatisticDateRange = new TabPane();

            // Tạo Tab 1 - Tổng quan
            Tab tab1 = new Tab("Tổng quan");
            VBox labelsContainer = new VBox(10);
            labelsContainer.setPadding(new Insets(20));

            // Thêm các thành phần vào container
            labelsContainer.getChildren().addAll(
                    createTitleLabel(dateStart, dateEnd),
                    createOverviewBoxByDateRange(dateStart,dateEnd),
                    createDetailTitle(),
                    createCategoryDateRange(dateStart,dateEnd)
            );

            tab1.setContent(labelsContainer);

            // Tạo Tab 2 - Biểu đồ tròn
            Tab tab2 = new Tab("Biểu đồ tròn");
            VBox pieChartContainer = new VBox(10);
            pieChartContainer.setPadding(new Insets(20));

            PieChart pieChart = createPieChartByDateRange(dateStart, dateEnd, expenses);

            pieChartContainer.getChildren().add(pieChart);
            tab2.setContent(pieChartContainer);

            // Tạo Tab 3 - Biểu đồ cột
            Tab tab3 = new Tab("Biểu đồ cột");
            VBox barChartContainer = new VBox(10);
            barChartContainer.setPadding(new Insets(20));

            BarChart<String, Number> barChart = createBarChart(expenses);

            barChartContainer.getChildren().add(barChart);
            tab3.setContent(barChartContainer);

            // Thêm các tab vào TabPane
            tabPaneStatisticDateRange.getTabs().addAll(tab1, tab2, tab3);

            // Tạo Scene và hiển thị
            Scene scene = new Scene(tabPaneStatisticDateRange, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Thống kê chi tiêu từ ngày " + dateStart+" đến ngày "+dateEnd);
            stage.show();

        } catch (Exception e) {
            logger.error("Lỗi khi hiển thị thống kê", e);
        }
    }

    private Label createTitleLabel(String date) {
        Label titleLabel = new Label("TỔNG QUAN CHI TIÊU NGÀY " + date);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setPadding(new Insets(0, 0, 10, 0));
        return titleLabel;
    }

    private Label createTitleLabel(String dateStart, String dateEnd) {
        Label titleLabel = new Label("TỔNG QUAN CHI TIÊU TỪ NGÀY " + dateStart+" ĐẾN NGÀY "+ dateEnd);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setPadding(new Insets(0, 0, 10, 0));
        return titleLabel;
    }

    private VBox createOverviewBoxByDate(String date) {
        VBox overviewBox = new VBox(5);
        overviewBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-radius: 5;");

        Label totalLabel = new Label("Tổng số tiền chi tiêu: "
                + ExpenseDAO.getTotalAmountExpensesByDate(date));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label maxAmount = new Label("Số tiền chi tiêu lớn nhất: "
                + ExpenseDAO.getMaxExpenseByDate(date));
        maxAmount.setStyle("-fx-font-size: 14px;");

        Label countExpense = new Label("Số lần chi tiêu: "
                + ExpenseDAO.getExpenseCountByDate(date));
        countExpense.setStyle("-fx-font-size: 14px;");

        String maxCategory = ExpenseDAO.getCategoryWithMaxAmountByDate(date);
        Label maxCategoryLabel = new Label("Danh mục chi tiêu nhiều nhất: "
                + maxCategory);
        maxCategoryLabel.setStyle("-fx-font-size: 14px;");

        Label maxCategoryAmount = new Label("Tổng số tiền cho danh mục này là: "
                + ExpenseDAO.getTotalExpensesByCategoryAndDate(maxCategory,date));
        maxCategoryAmount.setStyle("-fx-font-size: 14px;");

        overviewBox.getChildren().addAll(totalLabel, maxAmount,countExpense, maxCategoryLabel, maxCategoryAmount);
        return overviewBox;
    }

    private VBox createOverviewBoxByDateRange(String dateStart, String dateEnd) {
        VBox overviewBox = new VBox(5);
        overviewBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-radius: 5;");

        Label totalLabel = new Label("Tổng số tiền chi tiêu: " + ExpenseDAO.getTotalExpensesByDateRange(dateStart, dateEnd));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label maxCount = new Label("Số lần chi tiêu: " + ExpenseDAO.getExpenseCountBetweenDates(dateStart, dateEnd) + " lần");
        maxCount.setStyle("-fx-font-size: 14px;");

        String maxCategory = ExpenseDAO.getCategoryWithMaxAmountBetweenDates(dateStart,dateEnd);
        Label maxCategoryLabel = new Label("Danh mục chi tiêu nhiều nhất: " + maxCategory);
        maxCategoryLabel.setStyle("-fx-font-size: 14px;");

        Label maxCategoryAmount = new Label("Tổng số tiền: " + ExpenseDAO.getTotalExpensesByCategoryInDateRange(maxCategory, dateStart,dateEnd));
        maxCategoryAmount.setStyle("-fx-font-size: 14px;");

        overviewBox.getChildren().addAll(totalLabel,maxCount, maxCategoryLabel, maxCategoryAmount);
        return overviewBox;
    }

    private Label createDetailTitle() {
        Label detailTitle = new Label("CHI TIẾT THEO DANH MỤC");
        detailTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        detailTitle.setPadding(new Insets(10, 0, 5, 0));
        return detailTitle;
    }

    private VBox createCategoryDetails(String date) {
        VBox categoryDetails = new VBox(5);
        categoryDetails.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-radius: 5;");

        // Lấy danh sách các category duy nhất
        Set<String> categories = new HashSet<>();
        for (Expense expense : ExpenseDAO.getExpensesByDate(date)) {
            categories.add(expense.getCategory());
        }

        // Thêm thông tin chi tiết cho mỗi category
        for (String category : categories) {
            int count = ExpenseDAO.getCategoryCountByDate(category, date);
            String amount = ExpenseDAO.getTotalExpensesByCategoryAndDate(category,date);

            VBox categoryBox = createCategoryBox(category, count, amount);
            categoryDetails.getChildren().add(categoryBox);
        }

        return categoryDetails;
    }

    private VBox createCategoryDateRange(String dateStart, String dateEnd) {
        VBox categoryDetails = new VBox(5);
        categoryDetails.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-radius: 5;");

        // Lấy danh sách các category duy nhất
        Set<String> categories = new HashSet<>();
        for (Expense expense : ExpenseDAO.getExpensesByDateRange(dateStart,dateEnd)) {
            categories.add(expense.getCategory());
        }

        // Thêm thông tin chi tiết cho mỗi category
        for (String category : categories) {
            int count = ExpenseDAO.getCategoryCountByDateRange(category, dateStart, dateEnd);
            String amount = ExpenseDAO.getTotalExpensesByCategory(category);

            VBox categoryBox = createCategoryBox(category, count, amount);
            categoryDetails.getChildren().add(categoryBox);
        }

        return categoryDetails;
    }

    private VBox createCategoryBox(String category, int count, String amount) {
        Label categoryLabel = new Label(category);
        categoryLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label countLabel = new Label("Số lần chi tiêu: " + count + " lần");
        Label amountLabel = new Label("Tổng số tiền: " + amount);

        VBox categoryBox = new VBox(3);
        categoryBox.setPadding(new Insets(5, 0, 5, 10));
        categoryBox.getChildren().addAll(categoryLabel, countLabel, amountLabel);

        return categoryBox;
    }

    private PieChart createPieChart(String date, List<Expense> expenses) {
        try {
            if (expenses.isEmpty()) {
                PieChart emptyChart = new PieChart();
                emptyChart.setTitle("Không có dữ liệu chi tiêu cho ngày " + date);
                return emptyChart;
            }

            // Tính tổng chi tiêu và gộp các chi tiêu cùng category
            double total = 0;
            Map<String, Double> categoryTotals = new HashMap<>();
            
            for (Expense expense : expenses) {
                String category = expense.getCategory();
                double amount = Double.parseDouble(expense.getAmount());
                total += amount;
                
                // Gộp số tiền cho các category giống nhau
                categoryTotals.merge(category, amount, Double::sum);
            }

            // Tạo dữ liệu cho biểu đồ tròn
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                double percentage = (entry.getValue() / total) * 100;
                pieChartData.add(new PieChart.Data(
                    entry.getKey() + " (" + String.format("%.1f", percentage) + "%)",
                    entry.getValue()
                ));
            }

            // Tạo biểu đồ tròn
            PieChart chart = new PieChart(pieChartData);
            chart.setTitle("Thống kê chi tiêu theo danh mục ngày " + date);
            chart.setLegendSide(Side.BOTTOM);
            chart.setStyle("-fx-font-size: 14px;");

            return chart;
        } catch (Exception e) {
            logger.error("Lỗi khi tạo biểu đồ tròn", e);
            PieChart errorChart = new PieChart();
            errorChart.setTitle("Lỗi khi tạo biểu đồ: " + e.getMessage());
            return errorChart;
        }
    }

    private PieChart createPieChartByDateRange(String dateStart, String dateEnd, List<Expense> expenses) {
        try {
            if (expenses.isEmpty()) {
                PieChart emptyChart = new PieChart();
                emptyChart.setTitle("Không có dữ liệu chi tiêu cho ngày " + dateStart+" đến ngày "+dateEnd);
                return emptyChart;
            }

            // Tính tổng chi tiêu và gộp các chi tiêu cùng category
            double total = 0;
            Map<String, Double> categoryTotals = new HashMap<>();

            for (Expense expense : expenses) {
                String category = expense.getCategory();
                double amount = Double.parseDouble(expense.getAmount());
                total += amount;

                // Gộp số tiền cho các category giống nhau
                categoryTotals.merge(category, amount, Double::sum);
            }

            // Tạo dữ liệu cho biểu đồ tròn
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                double percentage = (entry.getValue() / total) * 100;
                pieChartData.add(new PieChart.Data(
                        entry.getKey() + " (" + String.format("%.1f", percentage) + "%)",
                        entry.getValue()
                ));
            }

            // Tạo biểu đồ tròn
            PieChart chart = new PieChart(pieChartData);
            chart.setTitle("Thống kê chi tiêu theo danh mục ngày " + dateStart+" đến ngày "+dateEnd);
            chart.setLegendSide(Side.BOTTOM);
            chart.setStyle("-fx-font-size: 14px;");

            return chart;
        } catch (Exception e) {
            logger.error("Lỗi khi tạo biểu đồ tròn", e);
            PieChart errorChart = new PieChart();
            errorChart.setTitle("Lỗi khi tạo biểu đồ: " + e.getMessage());
            return errorChart;
        }
    }

    private BarChart<String, Number> createBarChart( List<Expense> expenses) {
        try {
            if (expenses.isEmpty()) {
                BarChart<String, Number> emptyChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
                emptyChart.setTitle("Không có dữ liệu chi tiêu");
                return emptyChart;
            }

            // Tính tổng chi tiêu theo category
            Map<String, Double> categoryTotals = new HashMap<>();
            for (Expense expense : expenses) {
                String category = expense.getCategory();
                double amount = Double.parseDouble(expense.getAmount());
                categoryTotals.merge(category, amount, Double::sum);
            }

            // Tạo trục cho biểu đồ cột
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Danh mục");
            yAxis.setLabel("Số tiền (VNĐ)");


            // Tạo biểu đồ cột
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Tổng chi tiêu theo danh mục");
            // Tạo dữ liệu cho biểu đồ cột
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Tổng chi tiêu");

            // Thêm dữ liệu cho mỗi category
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChart.getData().add(series);
            barChart.setStyle("-fx-font-size: 14px;");

            return barChart;
        } catch (Exception e) {
            logger.error("Lỗi khi tạo biểu đồ cột", e);
            BarChart<String, Number> errorChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
            errorChart.setTitle("Lỗi khi tạo biểu đồ: " + e.getMessage());
            return errorChart;
        }
    }
} 