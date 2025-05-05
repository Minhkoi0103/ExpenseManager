module com.expensemanager.expensemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;

    opens com.expensemanager.module to javafx.base;
    exports com.expensemanager.controller;
    opens com.expensemanager.controller to javafx.fxml;
    exports com.expensemanager;
    opens com.expensemanager to javafx.fxml;
    opens com.expensemanager.util to javafx.base;
}