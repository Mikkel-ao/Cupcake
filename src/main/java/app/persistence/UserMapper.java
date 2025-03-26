package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import app.persistence.ConnectionPool;

public class UserMapper {


    // Authenticates the user by verifying username and password from the database
    public static User login(String password, String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users where email=? and password=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, password);
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                String role = rs.getString("role");
                double balance = rs.getDouble("balance");
                return new User(user_id, password, email, role, balance);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    // Creates a new user by inserting username and password into the database
    public static void createUser(String password, String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (password, email) values (?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger");
            }
        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static double getUserBalance(ConnectionPool connectionPool, int userId) throws DatabaseException {
        String sql = "SELECT balance FROM users WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                throw new DatabaseException("User not found for ID: " + userId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve user balance: " + e.getMessage());
        }
    }

    public static void updateUserBalance(ConnectionPool connectionPool, int userId, double newBalance) throws DatabaseException {
        String sql = "UPDATE users SET balance = ? WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to update balance for user ID: " + userId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user balance: " + e.getMessage());
        }
    }

}
