package app.persistence;

import app.DTO.UserAndOrder;
import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static List<UserAndOrder> getOrdersByRole(ConnectionPool connectionPool, int userId, String role) throws DatabaseException
    {
        List<UserAndOrder> orderList = new ArrayList<>();
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
                orderList.add(new UserAndOrder(orderId, email, bottomName, toppingName, timestamp, price, quantity));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl!!!!", e.getMessage());
        }
        return orderList;
    }

}
