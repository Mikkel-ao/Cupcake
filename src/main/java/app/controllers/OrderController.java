package app.controllers;

import app.DTO.BasketItemDTO;
import app.DTO.BasketOrder;
import app.DTO.UserAndOrderDTO;
import app.entities.OrderDetails;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.post("orders", ctx -> showAllOrders(ctx, connectionPool));
        app.get("/orders", ctx -> showAllOrders(ctx, connectionPool)); //TODO: This must be deleted! Only for testing purposes!
        app.post("add-to-basket",ctx -> addToBasket(ctx,connectionPool));
        app.get("checkout", ctx -> viewBasket(ctx,connectionPool));
        app.post("checkout", ctx -> checkout(ctx,connectionPool));
    }

    private static void checkout(Context ctx, ConnectionPool connectionPool) {
        HttpSession session = ctx.req().getSession();

        // Retrieve the basket from the session
        List<BasketItemDTO> basket = (List<BasketItemDTO>) session.getAttribute("basket");
        if (basket == null || basket.isEmpty()) {
            ctx.status(400).result("Your basket is empty!");
            return;
        }

        try {
            // Insert the order and get the generated order_id
            int orderId = OrderMapper.createOrder(connectionPool);

            // Save each basket item in the order_details table
            for (BasketItemDTO item : basket) {
                OrderDetails orderDetail = new OrderDetails(orderId, item.getBottomId(), item.getToppingId(), item.getQuantity(), item.getPrice());
                OrderMapper.saveOrderDetail(connectionPool, orderDetail);
            }

            // Clear the basket after purchase
            session.removeAttribute("basket");

            // Skal laves om
            ctx.redirect("/receipt?orderId=" + orderId);
        } catch (Exception e) {
            ctx.status(500).result("Failed to process the order: " + e.getMessage());
        }
    }



    private static void viewBasket(Context ctx, ConnectionPool connectionPool) {

        HttpSession session = ctx.req().getSession();
        List<BasketOrder> basket = (List<BasketOrder>) session.getAttribute("basket");
        if(basket == null){
            basket = new ArrayList<>();
        }

        ctx.attribute("basket", basket);
        ctx.render("checkout.html");
    }

    private static void addToBasket(Context ctx, ConnectionPool connectionPool) {
        HttpSession session = ctx.req().getSession();

        List<BasketItemDTO> basket = (List<BasketItemDTO>) session.getAttribute("basket");
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
        session.setAttribute("basket", basket);
        ctx.redirect("/");
    }

    private static void showAllOrders(Context ctx, ConnectionPool connectionPool) {
        try {

            //TODO: These values are hard coded for testing purposes so far!
            int userId = 1; //ctx.sessionAttribute("userId");
            String role = "customer";//ctx.sessionAttribute("role");


            // Fetch the orders using the OrderMapper
            List<UserAndOrderDTO> orderList = OrderMapper.getOrdersByRole(connectionPool, userId, role);

            // Pass the order list to the HTML template
            ctx.attribute("orderList", orderList);

            // Render the HTML page with Thymeleaf
            ctx.render("orders.html");
        } catch (Exception e) {
            ctx.status(500).result("An error occurred while fetching orders: " + e.getMessage());
        }
    }

}
