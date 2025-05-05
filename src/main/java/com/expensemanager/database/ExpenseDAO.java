package com.expensemanager.database;

import com.expensemanager.module.Expense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.expensemanager.database.DataBaseManager.closeConnection;
import static com.expensemanager.database.DataBaseManager.getConnection;

public class ExpenseDAO {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseDAO.class);

    public static void addExpense(com.expensemanager.module.Expense expense) {
        String sql = "INSERT INTO expenses(id,amount, category, date) VALUES(?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            /*String formattedAmount = NumberFormat.getInstance(Locale.US)
                    .format(Double.parseDouble(expense.getAmount()));*/

            pstmt.setString(1, expense.getId());
            pstmt.setString(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDate());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Added new expense: amount={}, category={}, date={}",
                        expense.getAmount(), expense.getCategory(), expense.getDate());
            }
        } catch (SQLException e) {
            logger.error("Error adding expense", e);
        } finally {
            closeConnection();
        }
    }

    public static void addExpenseList(List<Expense> expenses) {
        String sql = "INSERT INTO expenses(id,amount, category, date) VALUES(?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Expense expense : expenses) {


                pstmt.setString(1, expense.getId());
                pstmt.setString(2, expense.getAmount());
                pstmt.setString(3, expense.getCategory());
                pstmt.setString(4, expense.getDate());

            }
            logger.info("Thêm thành công {} chi tiêu", expenses.size());
        } catch (SQLException e) {
            logger.error("Error adding expense", e);
        } finally {
            closeConnection();
        }
    }

    public static int countExpenses() {
        String sql = "SELECT COUNT(*) FROM expenses";

        int result = 0;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = rs.getInt(1); // Lấy số lượng dòng
            }
        } catch (SQLException e) {
            logger.error("Error counting expenses", e);
        } finally {
            closeConnection();
        }
        return result;
    }

    public static int getCategoryCountByDate(String category, String date) {
        String sql = "SELECT COUNT(*) as count FROM expenses WHERE category = ? AND date = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set giá trị cho tham số category và date
            pstmt.setString(1, category);
            pstmt.setString(2, date);

            // Thực thi truy vấn
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count"); // Trả về số lượng bản ghi của category trong ngày
            }

        } catch (SQLException e) {
            logger.error("Error getting category count for category: " + category + " on date: " + date, e);
        } finally {
            closeConnection();
        }

        return 0; // Trả về 0 nếu không có chi tiêu cho category trong ngày đó
    }

    public static int getCategoryCountByDateRange(String category, String startDate, String endDate) {
        String sql = "SELECT COUNT(*) as count FROM expenses WHERE category = ? AND date BETWEEN ? AND ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count"); // Số bản ghi của category trong khoảng thời gian
            }

        } catch (SQLException e) {
            logger.error("Error getting category count for category: " + category + " from " + startDate + " to " + endDate, e);
        } finally {
            closeConnection();
        }

        return 0;
    }

    public static int getExpenseCountBetweenDates(String startDate, String endDate) {
        String sql = "SELECT COUNT(*) AS count FROM expenses WHERE date BETWEEN ? AND ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count"); // Trả về số lần chi tiêu
            }

        } catch (SQLException e) {
            logger.error("Error getting spending count from " + startDate + " to " + endDate, e);
        } finally {
            closeConnection();
        }

        return 0; // Trả về 0 nếu không có dữ liệu
    }


    public static int getExpenseCountByDate(String date) {
        String sql = "SELECT COUNT(*) AS count FROM expenses WHERE date = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set giá trị cho tham số date
            pstmt.setString(1, date);

            // Thực thi truy vấn
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count"); // Trả về số lần chi tiêu trong ngày
            }

        } catch (SQLException e) {
            logger.error("Error getting spending count for date: " + date, e);
        } finally {
            closeConnection();
        }

        return 0; // Trả về 0 nếu không có chi tiêu trong ngày đó
    }

    public static List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY date DESC";

        try (Connection conn = DataBaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getString("id"),
                        rs.getString("amount"),
                        rs.getString("category"),
                        rs.getString("date")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            logger.error("Error getting all expenses", e);
        } finally {
            closeConnection();
        }
        return expenses;
    }

    public static String getCategoryWithMaxAmountByDate(String date) {
        String sql = "SELECT category, SUM(amount) AS total_amount " +
                "FROM expenses " +
                "WHERE date = ? " +
                "GROUP BY category " +
                "ORDER BY total_amount DESC LIMIT 1";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set giá trị cho tham số date
            pstmt.setString(1, date);

            // Thực thi truy vấn
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("category"); // Trả về category có tổng amount lớn nhất
            }

        } catch (SQLException e) {
            logger.error("Error getting category with max amount for date: " + date, e);
        } finally {
            closeConnection();
        }

        return null; // Trả về null nếu không có dữ liệu cho ngày đó
    }

    public static List<Expense> getExpensesByCategory(String category) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE category = ? ORDER BY date DESC";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getString("id"),
                        rs.getString("amount"),
                        rs.getString("category"),
                        rs.getString("date")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            logger.error("Error getting expenses by category", e);
        }
        return expenses;
    }

    public static List<Expense> getExpensesByDate(String date) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE date = ? ORDER BY date DESC";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getString("id"),
                        rs.getString("amount").replaceAll(",", ""),
                        rs.getString("category"),
                        rs.getString("date")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            logger.error("Error getting expenses by category", e);
        } finally {
            closeConnection();
        }
        return expenses;
    }

    public static List<Expense> getExpensesByDateRange(String startDate, String endDate) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE date BETWEEN ? AND ? ORDER BY date DESC";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getString("id"),
                        rs.getString("amount").replaceAll(",", ""),
                        rs.getString("category"),
                        rs.getString("date")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            logger.error("Error getting expenses by date range", e);
        } finally {
            closeConnection();
        }
        return expenses;
    }

    public static void deleteExpenseById(String id) {
        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            /*int affectedRows =*/
            pstmt.executeUpdate();
            //return affectedRows > 0;
            logger.info("Xóa thành công");
        } catch (SQLException e) {
            logger.error("Error deleting expense", e);
            //return false;
        } finally {
            closeConnection();
        }
    }

    public static void deleteAllExpenses() {
        String sql = "DELETE FROM expenses";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("All expenses have been deleted.");
            }
        } catch (SQLException e) {
            logger.error("Error deleting all expenses", e);
        } finally {
            closeConnection();
        }
    }

    public static String getTotalAmountExpensesByDate(String date) {
        String sql = "SELECT SUM(amount) as total FROM expenses WHERE date = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set giá trị cho tham số ngày
            pstmt.setString(1, date);

            // Thực thi truy vấn
            ResultSet rs = pstmt.executeQuery();

            // Kiểm tra nếu có dữ liệu trả về
            if (rs.next()) {
                return rs.getString("total"); // trả về tổng chi tiêu dạng String
            }

        } catch (SQLException e) {
            logger.error("Error getting total expenses for date: " + date, e);
        } finally {
            closeConnection(); // Đảm bảo đóng kết nối sau khi xong
        }

        // Trả về "0.0" nếu không có chi
        return "0.0";
    }

    public static String getTotalExpensesByDateRange(String startDate, String endDate) {
        String sql = "SELECT SUM(amount) as total FROM expenses WHERE date BETWEEN ? AND ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total");
                if (!rs.wasNull()) {
                    int totalInt = (int) total; // Ép kiểu về int
                    return String.valueOf(totalInt);
                }
            }

        } catch (SQLException e) {
            logger.error("Error getting total expenses for date range: " + startDate + " to " + endDate, e);
        } finally {
            closeConnection();
        }

        return "0.0";
    }

    public static int getMaxExpenseByDate(String date) {
        String sql = "SELECT MAX(amount) AS max_amount FROM expenses WHERE date = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date); // Gán tham số ngày

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("max_amount"); // Trả về số tiền lớn nhất (dạng int)
            }

        } catch (SQLException e) {
            logger.error("Error getting max expense for date: " + date, e);
        } finally {
            closeConnection();
        }

        return 0; // Trả về 0 nếu không có dữ liệu
    }

    public static String getCategoryWithMaxAmountBetweenDates(String startDate, String endDate) {
        String sql = "SELECT category, SUM(amount) AS total_amount " +
                "FROM expenses " +
                "WHERE date BETWEEN ? AND ? " +
                "GROUP BY category " +
                "ORDER BY total_amount DESC LIMIT 1";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("category"); // Trả về category có tổng amount lớn nhất
            }

        } catch (SQLException e) {
            logger.error("Error getting category with max amount from " + startDate + " to " + endDate, e);
        } finally {
            closeConnection();
        }

        return null; // Trả về null nếu không có dữ liệu
    }

    public static String getTotalExpensesByCategory(String category) {
        String sql = "SELECT SUM(amount) as total FROM expenses WHERE category = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set giá trị cho tham số category
            pstmt.setString(1, category);

            // Thực thi truy vấn
            ResultSet rs = pstmt.executeQuery();

            // Kiểm tra nếu có dữ liệu trả về
            if (rs.next()) {
                double total = rs.getDouble("total");
                if (!rs.wasNull()) {
                    int totalInt = (int) total; // Ép kiểu về int
                    return String.valueOf(totalInt);
                } // trả về tổng chi tiêu theo category
            }

        } catch (SQLException e) {
            logger.error("Error getting total expenses for category: " + category, e);
        } finally {
            closeConnection(); // Đảm bảo đóng kết nối sau khi xong
        }

        // Trả về "0.0" nếu không có chi tiêu trong category
        return "0.0";
    }

    public static String getTotalExpensesByCategoryAndDate(String category, String date) {
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE category = ? AND date = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setString(2, date);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total");
                if (!rs.wasNull()) {
                    int totalInt = (int) total; // Ép kiểu về int
                    return String.valueOf(totalInt);
                }
            }

        } catch (SQLException e) {
            logger.error("Error getting total expenses for category: " + category + " on date: " + date, e);
        }finally{
            closeConnection();
        }

        return "0"; // Trả về "0" nếu không có dữ liệu
    }

    public static String getTotalExpensesByCategoryInDateRange(String category, String startDate, String endDate) {
        String sql = "SELECT SUM(amount) as total FROM expenses WHERE category = ? AND date BETWEEN ? AND ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total");
                if (!rs.wasNull()) {
                    int totalInt = (int) total; // Ép kiểu về int
                    return String.valueOf(totalInt);
                }
            }

        } catch (SQLException e) {
            logger.error("Error getting total expenses for category: " + category + " from " + startDate + " to " + endDate, e);
        } finally {
            closeConnection();
        }

        return "0.0";
    }

}