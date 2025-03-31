package app.persistence;

import app.DTO.BasketItemDTO;
import app.DTO.UserAndOrderDTO;
import app.entities.OrderDetails;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {


    //Method for getting order details for a single order from database
    public static List<BasketItemDTO> getOrderDetailsByOrderId(ConnectionPool connectionPool, int orderId) throws DatabaseException {

        String sql = "SELECT bottom_id, topping_id, quantity, cupcake_price FROM order_details WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId); //Setting order id as the one from the parameter
            ResultSet rs = ps.executeQuery();

            //creating list to hold instances of our DTO
            List<BasketItemDTO> orderDetailsList = new ArrayList<>();
            //Iterating until there are no more rows in database
            while (rs.next()) {
                //Assigning data retrieved from database query
                int bottomId = rs.getInt("bottom_id");
                int toppingId = rs.getInt("topping_id");
                int quantity = rs.getInt("quantity");
                double cupcakePrice = rs.getDouble("cupcake_price");

                //Assigning data retrieved from two other mapper methods
                String bottomName = getBottomNameById(connectionPool, bottomId);
                String toppingName = getToppingNameById(connectionPool, toppingId);

                //Adding each instance to the list
                BasketItemDTO orderDetail = new BasketItemDTO(bottomId, bottomName, toppingId, toppingName, quantity, cupcakePrice);
                orderDetailsList.add(orderDetail);
            }
            //Returning the complete list of DTOs
            return orderDetailsList;
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordredetaljer for ordre med ordrenummer: " + orderId + "!");
        }
    }

    //Method for getting the name of the bottom part of the cupcake by its ID from database
    public static String getBottomNameById(ConnectionPool connectionPool, int bottomId) throws DatabaseException {

        String sql = "SELECT bottom_name FROM cupcake_bottoms WHERE bottom_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bottomId); //Setting bottom id as the one from the parameter
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { //Returning the String of the column "bottom_name" from database (if it exists)
                return rs.getString("bottom_name");
            } else {
                throw new DatabaseException("Bottom med ID: " + bottomId + " blev ikke fundet!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente navn på bottom!", e.getMessage());
        }
    }

    //Method for getting the name of the top part of the cupcake by its ID from database
    public static String getToppingNameById(ConnectionPool connectionPool, int toppingId) throws DatabaseException {
        String sql = "SELECT topping_name FROM cupcake_toppings WHERE topping_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, toppingId); //Setting topping id as the one from the parameter
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("topping_name"); //Returning the String of the column "topping_name" from database (if it exists)
            } else {
                throw new DatabaseException("Topping med ID: " + toppingId + " blev ikke fundet!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente navn på topping!", e.getMessage());
        }
    }



    //Method for getting the price of the topping part of cupcake from database
    public static double getToppingPrice(ConnectionPool connectionPool, String toppingName) throws DatabaseException {
        String sql = "SELECT price FROM cupcake_toppings WHERE topping_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, toppingName); //Setting "topping_name" as the String given as function argument!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price");  //Returning the price from database (if it exists)
            } else {
                throw new DatabaseException("Topping blev ikke fundet: " + toppingName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente pris på topping!", e.getMessage());
        }
    }

    //Method for getting the price of the bottom part of cupcake from database
    public static double getBottomPrice(ConnectionPool connectionPool, String bottomName) throws DatabaseException {
        String sql = "SELECT price FROM cupcake_bottoms WHERE bottom_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, bottomName); //Setting "bottom_name" as the String given as function argument!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price"); //Returning the price from database (if it exists)
            } else {
                throw new DatabaseException("Bottom blev ikke fundet: " + bottomName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente pris på bottom!", e.getMessage());
        }
    }


    //Method for deleting a single order from database
    public static boolean deleteOrderById(ConnectionPool connectionPool, int orderId) throws DatabaseException {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId); //Setting "order_id" in sql query as the one given as function argument!

            int affectedRows = ps.executeUpdate(); //Executing query and saves the amount of rows was affected in local variable!
            return affectedRows > 0; //Only returns true if at least 1 row was affected!
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke slette ordren!", e.getMessage());
        }
    }



    //Method for getting the id of the bottom part of cupcake from database
    public static int getBottomId(ConnectionPool connectionPool, String bottomName) throws DatabaseException {
        String sql = "SELECT bottom_id FROM cupcake_bottoms WHERE bottom_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, bottomName); //Setting "bottom_name" in sql query as the one given as function argument!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { //Checking whether there is a row in database to retrieve data from
                return rs.getInt("bottom_id"); //Returning the bottom_id
            } else {
                throw new DatabaseException("Bottom blev ikke fundet: " + bottomName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente bottom ID", e.getMessage());
        }
    }

    //Method for getting the id of the topping part of cupcake from database
    public static int getToppingId(ConnectionPool connectionPool, String toppingName) throws DatabaseException {
        String sql = "SELECT topping_id FROM cupcake_toppings WHERE topping_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, toppingName); //Setting "topping_name" in sql query as the one given as function argument!
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { //Checking whether there is a row in database to retrieve data from
                return rs.getInt("topping_id"); //Returning the bottom_id
            } else {
                throw new DatabaseException("Topping blev ikke fundet: " + toppingName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente topping ID", e.getMessage());
        }
    }


    //Method for saving order details in "order_details" table in database
    public static void saveOrderDetail(ConnectionPool connectionPool, OrderDetails item) throws DatabaseException {
        String sql = "INSERT INTO order_details (order_id, bottom_id, topping_id, quantity, cupcake_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            //Using the getter methods of "item" given as argument, to set values and execute insert into database
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getBottomId());
            ps.setInt(3, item.getToppingId());
            ps.setInt(4, item.getQuantity());
            ps.setDouble(5, item.getCupcakePrice());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke gemme ordredetaljerne!", e.getMessage());
        }
    }


    //Method for creating an order and retrieve the auto-generated order id
    public static int createOrder(ConnectionPool connectionPool, int userId) throws DatabaseException {
        String sql = "INSERT INTO orders (user_id, order_date) VALUES (?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { //Instructs database to return auto-generated keys (user_id) when executing insert statement!

            ps.setInt(1, userId); //Setting "user_id" in sql query as the one given as function argument!
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis())); //Setting "order_date" in sql query (which is datatype TimeStamp) to the current time!
            ps.executeUpdate(); //Executing sql query

            ResultSet rs = ps.getGeneratedKeys(); //Retrieving the auto-generated key from the database
            if (rs.next()) { //Check if they key was in fact returned!
                return rs.getInt(1); //Returning key of the first column in database (which is the order id)
            } else {
                throw new DatabaseException("Kunne ikke oprette ordren, da ingen genereret nøgle blev fundet!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette ordren!", e.getMessage());
        }
    }

    //Method for getting orders corresponding to the role of the logged-in user!
    public static List<UserAndOrderDTO> getOrdersByRole(ConnectionPool connectionPool, int userId, String role) throws DatabaseException
    {
        List<UserAndOrderDTO> orderList = new ArrayList<>();
        String sql;

        //Sql queries depending on the "role" of the logged-in user given in the argument!
        if("admin".equals(role)){
            sql = "SELECT orders.order_id, users.email, orders.order_date,  SUM(order_details.cupcake_price) AS total_price\n" +
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
            throw new DatabaseException("Ugyldig rolle!");
        }

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {         if ("customer".equals(role)) { //if the logged-in user is a "customer" we need to insert the user_id in sql query, to only get the right orders!
            ps.setInt(1, userId);
        }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) //Iterating through the rows of the database, creating instances of our DTO class, adding them to a list and return the list in the end!
            {
                int orderId = rs.getInt("order_id");
                String email = rs.getString("email");
                Timestamp timestamp = rs.getTimestamp("order_date");
                double totalPrice = rs.getDouble("total_price");
                orderList.add(new UserAndOrderDTO(orderId, email, timestamp, totalPrice));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Kunne ikke hente ordrerne!", e.getMessage());
        }
        return orderList;
    }

    public static List<BasketItemDTO> getOrderDetails(ConnectionPool connectionPool, String role, int userId, int orderId) throws DatabaseException {

        String sql;
        //Sql queries depending on the "role" of the logged-in user given in the argument!
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
            throw new DatabaseException("Ugyldig rolle!");
        }

        List<BasketItemDTO> orderDetailsList = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            //If statements, which confirms roles, and uses function arguments to complete the sql queries!
            if("admin".equals(role)) {
                ps.setInt(1, orderId);
            } else if ("customer".equals(role)) {
                ps.setInt(1, userId);
                ps.setInt(2, orderId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) { //Iterating through the rows of the database, creating instances of our DTO class, adding them to a list and return the list in the end!
                String bottomName = rs.getString("bottom_name");
                String toppingName = rs.getString("topping_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("cupcake_price");

                orderDetailsList.add(new BasketItemDTO(0, bottomName, 0, toppingName, quantity, price));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrerdetaljer!", e.getMessage());
        }

        return orderDetailsList;
    }


}
