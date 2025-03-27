package app.controllers;

import app.DTO.BasketItemDTO;
import app.DTO.BasketOrder;
import app.DTO.UserAndOrderDTO;
import app.entities.OrderDetails;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.post("orders", ctx -> showAllOrders(ctx, connectionPool)); //TODO: Denne bruges ikke indtil videre, men venter med at slette!
        app.get("orders", ctx -> showAllOrders(ctx, connectionPool));
        app.post("add-to-basket", ctx -> addToBasket(ctx, connectionPool));
        app.get("checkout", ctx -> viewBasket(ctx, connectionPool));
        app.post("checkout", ctx -> checkout(ctx, connectionPool));
        app.get("receipt", ctx -> showReceipt(ctx, connectionPool));
        app.post("cancel-order", ctx -> cancelOrder(ctx));
    }

    private static void cancelOrder(Context ctx) {
        ctx.sessionAttribute("basket", null);
        ctx.redirect("/");
    }


    private static void showReceipt(Context ctx, ConnectionPool connectionPool) {

        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));

            List<BasketItemDTO> orderDetails = OrderMapper.getOrderDetailsByOrderId(connectionPool, orderId);

            ctx.attribute("orderID", orderId);
            ctx.attribute("orderDetails", orderDetails);

            ctx.render("receipt.html");
        } catch (Exception e) {
            ctx.status(500).result(e.getMessage());
        }
    }

    private static void checkout(Context ctx, ConnectionPool connectionPool) {

        List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
        if (basket == null || basket.isEmpty()) {
            ctx.status(400).result("Basket is empty");
            return;
        }
        try {
            int userId = ctx.sessionAttribute("userId");

            if (userId == 0) {
                ctx.status(400).result("You are not logged in");
                return;
            }

            double totalPrice = 0;

            for (BasketItemDTO item : basket) {
                totalPrice += item.getPrice();
            }

            double currentBalance = UserMapper.getUserBalance(connectionPool, userId);

            if (currentBalance < totalPrice) {
                ctx.status(400).result("You do not have enough money");
                return;
            }

            double newBalance = currentBalance - totalPrice;
            UserMapper.updateUserBalance(connectionPool, userId, newBalance);



            int orderId = OrderMapper.createOrder(connectionPool, userId);


            for (BasketItemDTO item : basket) {
                OrderDetails orderDetail = new OrderDetails(orderId, item.getBottomId(), item.getToppingId(), item.getQuantity(), item.getPrice());
                OrderMapper.saveOrderDetail(connectionPool, orderDetail);
            }

            List<BasketItemDTO> orderDetails = OrderMapper.getOrderDetailsByOrderId(connectionPool, orderId);


            ctx.sessionAttribute("basket", null);

            ctx.attribute("orderId", orderId);
            ctx.attribute("orderDetails", orderDetails);

            ctx.render("receipt.html");
        } catch (Exception e) {
            ctx.status(500).result("Failed to process the order: " + e.getMessage());
        }
    }


    private static void viewBasket(Context ctx, ConnectionPool connectionPool) {
        List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
        if (basket == null) {
            basket = new ArrayList<>();
        }

        double totalPrice = 0;

        for(BasketItemDTO order : basket) {
            totalPrice += order.getPrice();
        }

        ctx.attribute("totalPrice", totalPrice);
        ctx.attribute("basket", basket);
        ctx.render("checkout.html");
    }

    private static void addToBasket(Context ctx, ConnectionPool connectionPool) {


        List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
        if (basket == null) {
            basket = new ArrayList<>();
        }


        String bottomName = ctx.formParam("bottom");
        String toppingName = ctx.formParam("topping");
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        int bottomId = OrderMapper.getBottomId(connectionPool, bottomName);
        int toppingId = OrderMapper.getToppingId(connectionPool, toppingName);

        double bottomPrice = OrderMapper.getBottomPrice(connectionPool, bottomName);
        double toppingPrice = OrderMapper.getToppingPrice(connectionPool, toppingName);
        double totalPrice = (bottomPrice + toppingPrice) * quantity;

        BasketItemDTO basketItem = new BasketItemDTO(bottomId, bottomName, toppingId, toppingName, quantity, totalPrice);
        basket.add(basketItem);
        ctx.sessionAttribute("basket", basket);
        ctx.redirect("/");
    }

    private static void showAllOrders(Context ctx, ConnectionPool connectionPool) {
        try {

            int userId = ctx.sessionAttribute("userId");
            String role = ctx.sessionAttribute("role");



            List<UserAndOrderDTO> orderList = OrderMapper.getOrdersByRole(connectionPool, userId, role);


            ctx.attribute("orderList", orderList);


            ctx.render("orders.html");
        } catch (Exception e) {
            ctx.status(500).result("An error occurred while fetching orders: " + e.getMessage());
        }
    }
}
