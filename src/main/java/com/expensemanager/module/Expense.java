package com.expensemanager.module;

import com.expensemanager.util.DateUtil;
import javafx.beans.property.SimpleStringProperty;


public class Expense {
    private final String id;
    private final SimpleStringProperty amount;
    private final SimpleStringProperty category;
    private final SimpleStringProperty date;


    public Expense(String amount, String category, String date) {
        this.id = System.currentTimeMillis()+"";
        this.amount = new SimpleStringProperty(amount);

        this.category = new SimpleStringProperty(category);

        this.date = new SimpleStringProperty(DateUtil.parseToLocalDate(date).toString());

        System.out.println("Đã tạo một Expense mới với id = " + getId() + " - amount = " + getAmount()
                + " - category = " + getCategory() + " - date = " + getDate());
    }

    public Expense(String id, String amount, String category, String date) {
        this.id = id;

        this.amount = new SimpleStringProperty(amount);

        this.category = new SimpleStringProperty(category);

        this.date = new SimpleStringProperty(DateUtil.parseToLocalDate(date).toString());

        System.out.println("Đã tạo một Expense mới với id = " + getId() + " - amount = " + getAmount()
                + " - category = " + getCategory() + " - date = " + getDate());
    }

    public String getAmount() {
        return this.amount.get();
    }

    public String getCategory() {
        return this.category.get();
    }

    public String getDate() {
        return this.date.get();
    }

    public String getId() {
        return this.id;
    }

}
