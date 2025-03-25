package app.controllers;

import app.DTO.UserAndOrder;
import app.entities.Order;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.post("orders", ctx -> showAllOrders(ctx, connectionPool));
        app.get("/orders", ctx -> showAllOrders(ctx, connectionPool));
    }

    private static void showAllOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            int userId = 1; //ctx.sessionAttribute("userId");
            String role = "customer";//ctx.sessionAttribute("role");


            // Fetch the orders using the OrderMapper
            List<UserAndOrder> orderList = OrderMapper.getOrdersByRole(connectionPool, userId, role);

            // Pass the order list to the HTML template
            ctx.attribute("orderList", orderList);

            // Render the HTML page with Thymeleaf
            ctx.render("orders.html");
        } catch (Exception e) {
            ctx.status(500).result("An error occurred while fetching orders: " + e.getMessage());
        }
    }

}
