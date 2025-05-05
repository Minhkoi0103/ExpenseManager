package com.expensemanager.util;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class DateUtil {

    // Danh sách các định dạng ngày tháng đầu vào có thể gặp
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    // Hàm chuẩn hóa chuỗi ngày tháng về LocalDate
    public static LocalDate parseToLocalDate(String input) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Định dạng ngày không hợp lệ: " + input);
    }

    public static String chooseDate( String title) {
        DatePicker datePicker = new DatePicker();

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);

        VBox vbox = new VBox(10, datePicker);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                LocalDate date = datePicker.getValue();
                if (date != null) {
                    return date.toString();
                } else {
                    return LocalDate.now().toString();
                }
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }


}
