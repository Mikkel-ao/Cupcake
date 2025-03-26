package app.persistence;

import app.DTO.UserDTO;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import app.persistence.ConnectionPool;

public class UserMapper {


    // Authenticates the user by verifying username and password from the database
    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users where email=? and password=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(2, email);
            ps.setString(1, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                String role = rs.getString("role");
                double balance = rs.getDouble("balance");
                return new User(user_id, password, email, role, balance);
            } else {
                // fejlen opstår fordi parametrene er andereledes i DB end i metoden.
                // når vi bytter virker path ikke
                // ved ikke hvordan vi kan ændre det
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
    public static List<UserDTO> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<UserDTO> customersList = new ArrayList<>();
        String sql = "SELECT user_id, email, balance FROM users";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
                ){
            while (rs.next()){
                int userId = rs.getInt("user_id");
                String email = rs.getString("email");
                double balance = rs.getDouble("balance");
                customersList.add(new UserDTO(userId,email,balance));
            }
        }catch (SQLException e) {
            throw new DatabaseException("Database Error", e.getMessage());
        }
        return customersList;
    }

}
