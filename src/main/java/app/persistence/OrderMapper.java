package app.persistence;

import app.DTO.BasketOrder;
import app.DTO.UserAndOrderDTO;
import app.entities.OrderDetails;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static double getToppingPrice(ConnectionPool connectionPool, String toppingName) throws DatabaseException {
        String sql = "SELECT price FROM cupcake_toppings WHERE topping_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, toppingName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price"); // Return the price of the topping
            } else {
                throw new DatabaseException("Topping not found: " + toppingName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve topping price: " + e.getMessage());
        }
    }


    public static double getBottomPrice(ConnectionPool connectionPool, String bottomName) throws DatabaseException {
        String sql = "SELECT price FROM cupcake_bottoms WHERE bottom_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, bottomName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price"); // Return the price of the bottom
            } else {
                throw new DatabaseException("Bottom not found: " + bottomName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve bottom price: " + e.getMessage());
        }
    }



    public static int getBottomId(ConnectionPool connectionPool, String bottomName) throws DatabaseException {
        String sql = "SELECT bottom_id FROM cupcake_bottoms WHERE bottom_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, bottomName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("bottom_id");
            } else {
                throw new DatabaseException("Bottom not found: " + bottomName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve bottom ID: " + e.getMessage());
        }
    }

    public static int getToppingId(ConnectionPool connectionPool, String toppingName) throws DatabaseException {
        String sql = "SELECT topping_id FROM cupcake_toppings WHERE topping_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, toppingName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("topping_id");
            } else {
                throw new DatabaseException("Topping not found: " + toppingName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve topping ID: " + e.getMessage());
        }
    }



    public static void saveOrderDetail(ConnectionPool connectionPool, OrderDetails item) throws DatabaseException {
        String sql = "INSERT INTO order_details (order_id, bottom_id, topping_id, quantity, cupcake_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, item.getOrder_id());
            ps.setInt(2, item.getBottom_id());
            ps.setInt(3, item.getTopping_id());
            ps.setInt(4, item.getQuantity());
            ps.setDouble(5, item.getCupcake_price());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save order detail: " + e.getMessage());
        }
    }



    public static int createOrder(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (order_date) VALUES (?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated order_id
            } else {
                throw new DatabaseException("Failed to create order, no ID generated.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create order: " + e.getMessage());
        }
    }


    public static List<UserAndOrderDTO> getOrdersByRole(ConnectionPool connectionPool, int userId, String role) throws DatabaseException
    {
        List<UserAndOrderDTO> orderList = new ArrayList<>();
        String sql;

        if("admin".equals(role)){
            sql = "SELECT orders.order_id, users.email, cupcake_bottoms.bottom_name, cupcake_toppings.topping_name, orders.order_date, order_details.cupcake_price, order_details.quantity\n" +
                    "FROM users\n" +
                    "JOIN orders ON orders.user_id = users.user_id\n" +
                    "JOIN order_details ON orders.order_id = order_details.order_id\n" +
                    "JOIN cupcake_bottoms ON cupcake_bottoms.bottom_id = order_details.bottom_id\n" +
                    "JOIN cupcake_toppings ON cupcake_toppings.topping_id = order_details.topping_id";
        } else if ("customer".equals(role)){
            sql = "SELECT orders.order_id, users.email, cupcake_bottoms.bottom_name, cupcake_toppings.topping_name, orders.order_date, order_details.cupcake_price, order_details.quantity\n" +
                    "FROM users\n" +
                    "JOIN orders ON orders.user_id = users.user_id\n" +
                    "JOIN order_details ON orders.order_id = order_details.order_id\n" +
                    "JOIN cupcake_bottoms ON cupcake_bottoms.bottom_id = order_details.bottom_id\n" +
                    "JOIN cupcake_toppings ON cupcake_toppings.topping_id = order_details.topping_id\n" +
                    "WHERE users.user_id = ?";
        } else {
            throw new DatabaseException("Invalid role!");
        }

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {         if ("customer".equals(role)) {
            ps.setInt(1, userId);
        }
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int orderId = rs.getInt("order_id");
                String email = rs.getString("email");
                String bottomName = rs.getString("bottom_name");
                String toppingName = rs.getString("topping_name");
                Timestamp timestamp = rs.getTimestamp("order_date");
                int price = rs.getInt("cupcake_price");
                int quantity = rs.getInt("quantity");
                orderList.add(new UserAndOrderDTO(orderId, email, bottomName, toppingName, timestamp, price, quantity));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl!!!!", e.getMessage());
        }
        return orderList;
    }

}
