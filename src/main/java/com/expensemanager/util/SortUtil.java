package com.expensemanager.util;

import com.expensemanager.module.Expense;

import java.time.LocalDate;
import java.util.List;

public class SortUtil {

    public enum SortOption {
        DATE_DESC, AMOUNT_DESC, DATE_ASC, AMOUNT_ASC
    }
    public static void sortExpenses(List<Expense> expenses, SortOption sortOption) {
        switch (sortOption) {
            case DATE_DESC:
                expenses.sort((e1, e2) -> {
                    // So sánh ngày trước (format: dd/MM/yyyy)
                    LocalDate date1 =LocalDate.parse( e1.getDate());
                    LocalDate date2 = LocalDate.parse(e2.getDate());
                        return date2.compareTo(date1);
                    //return date2.compareTo(date1);
                });
                break;
            case DATE_ASC:
                expenses.sort((e1, e2) -> {
                    LocalDate date1 =LocalDate.parse( e1.getDate());
                    LocalDate date2 = LocalDate.parse(e2.getDate());
                    return date1.compareTo(date2);

                });
                break;

            case AMOUNT_DESC:
                expenses.sort((e1, e2) -> {
                    double amount1 = Double.parseDouble(e1.getAmount().replaceAll(",",""));
                    double amount2 = Double.parseDouble(e2.getAmount().replaceAll(",",""));
                    return Double.compare(amount2, amount1);
                });
                break;
            case AMOUNT_ASC:
                expenses.sort((e1, e2) -> {
                    double amount1 = Double.parseDouble(e1.getAmount().replaceAll(",",""));
                    double amount2 = Double.parseDouble(e2.getAmount().replaceAll(",",""));
                    return Double.compare(amount1, amount2);
                });
                break;
        }
    }
}
