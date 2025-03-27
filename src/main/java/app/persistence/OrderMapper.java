package app.persistence;

import app.DTO.BasketItemDTO;
import app.DTO.UserAndOrderDTO;
import app.entities.OrderDetails;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static List<BasketItemDTO> getOrderDetailsByOrderId(ConnectionPool connectionPool, int orderId) throws DatabaseException {
        String sql = "SELECT bottom_id, topping_id, quantity, cupcake_price FROM order_details WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            List<BasketItemDTO> orderDetailsList = new ArrayList<>();
            while (rs.next()) {
                int bottomId = rs.getInt("bottom_id");
                int toppingId = rs.getInt("topping_id");
                int quantity = rs.getInt("quantity");
                double cupcakePrice = rs.getDouble("cupcake_price");


                String bottomName = getBottomNameById(connectionPool, bottomId);
                String toppingName = getToppingNameById(connectionPool, toppingId);


                BasketItemDTO orderDetail = new BasketItemDTO(bottomId, bottomName, toppingId, toppingName, quantity, cupcakePrice);
                orderDetailsList.add(orderDetail);
            }

            return orderDetailsList;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve order details for order ID: " + orderId);
        }
    }

    public static String getBottomNameById(ConnectionPool connectionPool, int bottomId) throws DatabaseException {
        String sql = "SELECT bottom_name FROM cupcake_bottoms WHERE bottom_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bottomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("bottom_name");
            } else {
                throw new DatabaseException("Bottom not found for ID: " + bottomId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve bottom name: " + e.getMessage());
        }
    }

    public static String getToppingNameById(ConnectionPool connectionPool, int toppingId) throws DatabaseException {
        String sql = "SELECT topping_name FROM cupcake_toppings WHERE topping_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, toppingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("topping_name");
            } else {
                throw new DatabaseException("Topping not found for ID: " + toppingId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve topping name: " + e.getMessage());
        }
    }




    public static double getToppingPrice(ConnectionPool connectionPool, String toppingName) throws DatabaseException {
        String sql = "SELECT price FROM cupcake_toppings WHERE topping_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, toppingName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price");
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
                return rs.getDouble("price");
            } else {
                throw new DatabaseException("Bottom not found: " + bottomName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve bottom price: " + e.getMessage());
        }
    }

    public static boolean deleteOrderById(ConnectionPool connectionPool, int orderId) throws DatabaseException {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete order.", e.getMessage());
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

            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getBottomId());
            ps.setInt(3, item.getToppingId());
            ps.setInt(4, item.getQuantity());
            ps.setDouble(5, item.getCupcakePrice());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save order detail: " + e.getMessage());
        }
    }



    public static int createOrder(ConnectionPool connectionPool, int userId) throws DatabaseException {
        String sql = "INSERT INTO orders (user_id, order_date) VALUES (?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new DatabaseException("Failed to create order");
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
            sql = "SELECT orders.order_id, users.email, orders.order_date,  SUM(DISTINCT order_details.cupcake_price * order_details.quantity) AS total_price\n" +
                    "                    FROM users\n" +
                    "                    JOIN orders ON orders.user_id = users.user_id\n" +
                    "                    JOIN order_details ON orders.order_id = order_details.order_id\n" +
                    "                    JOIN cupcake_bottoms ON cupcake_bottoms.bottom_id = order_details.bottom_id\n" +
                    "                    JOIN cupcake_toppings ON cupcake_toppings.topping_id = order_details.topping_id\n" +
                    "GROUP BY orders.order_id, users.email, orders.order_date\n" +
                    "ORDER BY orders.order_id";
        } else if ("customer".equals(role)){
            sql = "SELECT orders.order_id, users.email, orders.order_date,  SUM(order_details.cupcake_price) AS total_price\n" +
                    "FROM users\n" +
                    "JOIN orders ON orders.user_id = users.user_id\n" +
                    "JOIN order_details ON orders.order_id = order_details.order_id\n" +
                    "JOIN cupcake_bottoms ON cupcake_bottoms.bottom_id = order_details.bottom_id\n" +
                    "JOIN cupcake_toppings ON cupcake_toppings.topping_id = order_details.topping_id\n" +
                    "WHERE users.user_id = ? " +
                    "GROUP BY orders.order_id, users.email, orders.order_date\n" +
                    "ORDER BY orders.order_id";
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
                //String bottomName = rs.getString("bottom_name");
                //String toppingName = rs.getString("topping_name");
                Timestamp timestamp = rs.getTimestamp("order_date");
                double totalPrice = rs.getDouble("total_price");
                //int quantity = rs.getInt("quantity");
                orderList.add(new UserAndOrderDTO(orderId, email, timestamp, totalPrice));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl!!!!", e.getMessage());
        }
        return orderList;
    }

    public static List<BasketItemDTO> getOrderDetails(ConnectionPool connectionPool, String role, int userId, int orderId) throws DatabaseException {

        String sql;

        if("admin".equals(role)){
            sql = "SELECT cupcake_bottoms.bottom_name, cupcake_toppings.topping_name, order_details.quantity, order_details.cupcake_price " +
                    "FROM users " +
                    "JOIN orders ON orders.user_id = users.user_id " +
                    "JOIN order_details ON orders.order_id = order_details.order_id " +
                    "JOIN cupcake_bottoms ON cupcake_bottoms.bottom_id = order_details.bottom_id " +
                    "JOIN cupcake_toppings ON cupcake_toppings.topping_id = order_details.topping_id " +
                    "WHERE orders.order_id = ?";
        } else if ("customer".equals(role)) {
            sql = "SELECT cupcake_bottoms.bottom_name, cupcake_toppings.topping_name, order_details.quantity, order_details.cupcake_price " +
                    "FROM users " +
                    "JOIN orders ON orders.user_id = users.user_id " +
                    "JOIN order_details ON orders.order_id = order_details.order_id " +
                    "JOIN cupcake_bottoms ON cupcake_bottoms.bottom_id = order_details.bottom_id " +
                    "JOIN cupcake_toppings ON cupcake_toppings.topping_id = order_details.topping_id " +
                    "WHERE users.user_id = ? AND orders.order_id = ?";
        } else {
            throw new DatabaseException("Invalid role!");
        }

        List<BasketItemDTO> orderDetailsList = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if("admin".equals(role)) {
                ps.setInt(1, orderId);
            } else if ("customer".equals(role)) {
                ps.setInt(1, userId);
                ps.setInt(2, orderId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String bottomName = rs.getString("bottom_name");
                String toppingName = rs.getString("topping_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("cupcake_price");

                orderDetailsList.add(new BasketItemDTO(0, bottomName, 0, toppingName, quantity, price));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get order details.", e.getMessage());
        }

        return orderDetailsList;
    }


}
