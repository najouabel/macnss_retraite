package macnss.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

public class tools {


    // Try to parse a string to an integer, return 0 if it's not a valid integer
    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Check if an email has a valid format
    // Return true if the email is valid, false if not
    public static boolean isValidEmailFormat(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    // Check if a password is valid
    public static boolean isValidPassword(String password) {
        String regex = "^(?=\\S+$).{8,}$";
        // Must have 8 characters or more
        // No whitespaces allowed
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return !matcher.matches();
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
    public static double percentageStringToDouble(String percentage) {
        try {
            // Remove the '%' character from the percentage string
            String cleanedPercentage = percentage.replaceAll("%", "");

            // Parse the cleaned string to a double and divide by 100
            double result = Double.parseDouble(cleanedPercentage) / 100.0;

            return result;
        } catch (NumberFormatException e) {
            // Handle invalid input, such as non-numeric or improperly formatted strings
            throw new IllegalArgumentException("Invalid percentage string: " + percentage);
        }
    }
    public static void insertSalaryHistory(Connection connection) {
        String insertSalaryHistorySQL = "INSERT INTO salary_history (employee_id, salary, date) VALUES (?, ?, ?)";

        try (PreparedStatement insertSalaryHistoryStatement = connection.prepareStatement(insertSalaryHistorySQL)) {
            int employeeId = 15;
            double salary = 20000.0;
            LocalDate startDate = LocalDate.of(2006, Month.JANUARY, 1);

            for (int i = 0; i < 156; i++) {
                insertSalaryHistoryStatement.setInt(1, employeeId);
                insertSalaryHistoryStatement.setDouble(2, salary);
                insertSalaryHistoryStatement.setDate(3, java.sql.Date.valueOf(startDate));

                int rowsAffected = insertSalaryHistoryStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Inserted row for date: " + startDate);
                }

                startDate = startDate.plusMonths(1); // Move to the next month
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
