package com.expensemanager;

import com.expensemanager.database.DataBaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ExpenseManagerApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseManagerApplication.class);

    @Override
    public void start(Stage stage) {
        try {
            DataBaseManager.initializeDatabase();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/expensemanager/HomeExpenseManagerView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            stage.setTitle("Quản lý chi tiêu");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(scene);
            stage.show();

            logger.info("Ứng dụng đã khởi động thành công");

        } catch (IOException e) {
            logger.error("Lỗi khi khởi động ứng dụng", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            logger.error("Lỗi khi khởi tạo ứng dụng", e);
            System.exit(1);
        }
    }
}
