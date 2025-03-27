package app.controllers;

import app.DTO.BasketItemDTO;
import app.DTO.UserAndOrderDTO;
import app.entities.OrderDetails;
import app.exceptions.DatabaseException;
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
        app.get("orderdetails", ctx -> viewOrderDetails(ctx, connectionPool));
        app.post("deleteorder", ctx -> deleteOrder(ctx, connectionPool));
        app.post("removeitem", ctx -> removeBasketItem(ctx));
    }

    private static void removeBasketItem(Context ctx) {
        try{
            int bottomId = Integer.parseInt(ctx.queryParam("bottomId"));
            int topId = Integer.parseInt(ctx.queryParam("toppingId"));

            List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
            if(basket == null){
                ctx.status(400).result("Basket is empty!");
                return;
            }
            basket.removeIf(item -> item.getBottomId() == bottomId && item.getToppingId() == topId);
            ctx.sessionAttribute("basket", basket);

            ctx.redirect("/checkout");
        } catch (IllegalArgumentException e) {
            ctx.attribute("errorMessage", "Noget gik galt! Prøv igen eller annullér ordrer og start forfra!");
            ctx.render("checkout.html");
        }
    }

    private static void deleteOrder(Context ctx, ConnectionPool connectionPool) {
        try{
            String role = ctx.sessionAttribute("role");
            if(!"admin".equals(role)) {
                ctx.attribute("errorMessage", "Du kan ikke slette, da du ikke er admin!");
                ctx.render("/index.html");
                return;
            }
            int orderId =Integer.parseInt(ctx.queryParam("orderId"));

            boolean isDeleted = OrderMapper.deleteOrderById(connectionPool, orderId);
            if(!isDeleted) {
                ctx.attribute("errorMessage", "Ordre blev ikke fundet!");
                ctx.render("orders.html");
            } else {
                ctx.redirect("/orders");
            }
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Noget gik galt! Kunne ikke slette ordre fra database!");
            ctx.render("orders.html");
        }
    }

    private static void viewOrderDetails(Context ctx, ConnectionPool connectionPool) {
        try{
            String role = ctx.sessionAttribute("role");
            int userId = ctx.sessionAttribute("userId");
            int orderId = Integer.parseInt(ctx.queryParam("orderId"));

            List<BasketItemDTO> orderDetails = OrderMapper.getOrderDetails(connectionPool, role, userId, orderId);

            ctx.attribute("orderDetails", orderDetails);
            ctx.attribute("orderId", orderId);

            ctx.render("orderdetails.html");

        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Noget gik galt! Kunne ikke hente ordredetaljerne fra databasen!");
            ctx.render("index.html");
        }
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
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Noget gik galt! Kunne ikke hente kvitteringen!");
            ctx.render("receipt.html");
        }
    }

    private static void checkout(Context ctx, ConnectionPool connectionPool) {

        List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
        if (basket == null || basket.isEmpty()) {
            ctx.attribute("errorMessage", "Kurven er tom!");
            ctx.render("checkout.html");
            return;
        }
        try {
            Integer userId = ctx.sessionAttribute("userId");
            if (userId == null) {
                ctx.attribute("errorMessage", "Du skal være logget ind for at gennemføre et køb!");
                ctx.render("checkout.html");
                return;
            }

            double totalPrice = 0;

            for (BasketItemDTO item : basket) {
                totalPrice += item.getPrice();
            }

            double currentBalance = UserMapper.getUserBalance(connectionPool, userId);

            if (currentBalance < totalPrice) {
                ctx.attribute("errorMessage", "Du har ikke nok penge på din konto til at gennemføre køb! Indsæt penge eller fjern nogle varer fra kurven!");
                ctx.render("index.html");
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
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Kunne ikke gennemføre køb - prøv igen!");
            ctx.render("checkout.html");
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

        try {
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
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Kunne ikke tilføje vare til indkøbskurv - prøv igen!");
            ctx.render("index.html");
        }
    }

    private static void showAllOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            Integer userId = ctx.sessionAttribute("userId");
            String role = ctx.sessionAttribute("role");

            if (userId == null) {
                ctx.attribute("errorMessage", "Du skal være logget ind for at se dine ordrer");
                ctx.render("index.html");
                return;
            }

            List<UserAndOrderDTO> orderList = OrderMapper.getOrdersByRole(connectionPool, userId, role);

            ctx.attribute("role", role);
            ctx.attribute("orderList", orderList);


            ctx.render("orders.html");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Kunne ikke vise dine ordrer!");
            ctx.render("index.html");
        }
    }
}
