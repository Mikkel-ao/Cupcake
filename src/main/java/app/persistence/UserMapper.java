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
    public static User login(String password, String email,  ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users where email=? and password_hash=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, password);
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                return new User(id, password, email);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    // Creates a new user by inserting username and password into the database
    public static void createUser(String password, String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (password_hash, email) values (?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, password);
            ps.setString(2, email);

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

}
