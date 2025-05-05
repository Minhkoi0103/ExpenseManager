package com.expensemanager.util;

import java.time.LocalDate;

public class InputUtil {
    public static boolean validateInput(String amount, String category, String date) {
        if (amount.isEmpty() || !isNumeric(amount)) {
            AlertUtils.showError("Lỗi", "Số tiền không hợp lệ");
            return false;
        }
        if (category.isEmpty()) {
            AlertUtils.showError("Lỗi", "Vui lòng nhập mục đích chi tiêu");
            return false;
        }
        if (!date.isEmpty() && LocalDate.now().isBefore(LocalDate.parse(date))) {
            AlertUtils.showError("Lỗi", "Ngày không hợp lệ");
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}
