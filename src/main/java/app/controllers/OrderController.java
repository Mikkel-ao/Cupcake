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


    //Adding routes
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.post("orders", ctx -> showAllOrders(ctx, connectionPool));
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


    //Method for removing a simple item from the basket
    private static void removeBasketItem(Context ctx) {
        try{
            //Getting the ID for the URL
            int bottomId = Integer.parseInt(ctx.queryParam("bottomId"));
            int topId = Integer.parseInt(ctx.queryParam("toppingId"));

            //Retrieving the basket from front end attribute
            List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
            if(basket == null){
                ctx.attribute("errorMessage", "Der blev ikke fundet en kurv!");
                ctx.render("index.html");
                return;
            }
            //Deletes the item if it fullfills the requirements (right side of -> of lambda expression)
            basket.removeIf(item -> item.getBottomId() == bottomId && item.getToppingId() == topId);
            ctx.sessionAttribute("basket", basket);

            ctx.redirect("/checkout");
        } catch (IllegalArgumentException e) {
            ctx.attribute("errorMessage", "Noget gik galt! Prøv igen eller annullér ordrer og start forfra!");
            ctx.render("checkout.html");
        }
    }

    //Method for deleting an order - only available for admins!
    private static void deleteOrder(Context ctx, ConnectionPool connectionPool) {
        try{
            //Getting the role from the session, and checking whether it's an admin or not!
            String role = ctx.sessionAttribute("role");
            if(!"admin".equals(role)) {
                ctx.attribute("errorMessage", "Du kan ikke slette, da du ikke er admin!");
                ctx.render("/index.html");
                return;
            }

            //Getting the order id from URL
            int orderId =Integer.parseInt(ctx.queryParam("orderId"));

            //Trying to delete order, and gives an error message if the operation's not completed!
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

    //Method for getting a detailed overview of a single order
    private static void viewOrderDetails(Context ctx, ConnectionPool connectionPool) {
        try{
            //Retrieving user information from the session attributes
            String role = ctx.sessionAttribute("role");
            int userId = ctx.sessionAttribute("userId");
            //Retrieving order id from URL
            int orderId = Integer.parseInt(ctx.queryParam("orderId"));

            //Getting the order details from database
            List<BasketItemDTO> orderDetails = OrderMapper.getOrderDetails(connectionPool, role, userId, orderId);


            //Sending the assigned values along to the new page, which is then loaded!
            ctx.attribute("orderDetails", orderDetails);
            ctx.attribute("orderId", orderId);

            ctx.render("orderdetails.html");

        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Noget gik galt! Kunne ikke hente ordredetaljerne fra databasen!");
            ctx.render("index.html");
        }
    }

    //A simple method for clearing the basket and redirecting user to home page
    private static void cancelOrder(Context ctx) {
        ctx.sessionAttribute("basket", null);
        ctx.redirect("/");
    }

    //Method for showing the customer an overview of the recent purchase
    private static void showReceipt(Context ctx, ConnectionPool connectionPool) {
        try {

            //Getting order id from session attributes, and checking whether it exists
            Integer orderId = ctx.sessionAttribute("orderId");
            if(orderId == null) {
                ctx.attribute("errorMessage", "Kvittering kunne ikke findes!");
                ctx.render("index.html");
            }

            //Getting data from database on the specific order
            List<BasketItemDTO> orderDetails = OrderMapper.getOrderDetailsByOrderId(connectionPool, orderId);

            //Passing along the data needed to load the receipt
            ctx.attribute("orderID", orderId);
            ctx.attribute("orderDetails", orderDetails);

            ctx.render("receipt.html");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Noget gik galt! Kunne ikke hente kvitteringen!");
            ctx.render("receipt.html");
        }
    }

    //Method for the actions happening when pressing "buy" in the checkout page
    private static void checkout(Context ctx, ConnectionPool connectionPool) {

        //Retrieving the basket from session and give an error message if you try to buy an empty basket!
        List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
        if (basket == null || basket.isEmpty()) {
            ctx.attribute("errorMessage", "Kurven er tom!");
            ctx.render("checkout.html");
            return;
        }
        try {
            //Retrieves the user id from session and give an error message if you try to buy without being logged in!
            Integer userId = ctx.sessionAttribute("userId");
            if (userId == null) {
                ctx.attribute("errorMessage", "Du skal være logget ind for at gennemføre et køb!");
                ctx.render("checkout.html");
                return;
            }

            //Iterating through items, to get the total price
            double totalPrice = 0;

            for (BasketItemDTO item : basket) {
                totalPrice += item.getPrice();
            }

            //Retrieves the balance of the current user from database
            double currentBalance = UserMapper.getUserBalance(connectionPool, userId);

            //Checking whether the user has enough money to complete purchase!
            if (currentBalance < totalPrice) {
                ctx.attribute("errorMessage", "Du har ikke nok penge på din konto til at gennemføre køb! Indsæt penge eller fjern nogle varer fra kurven!");
                ctx.render("index.html");
                return;
            }

            //Subtracting the amount of the purchase from the user's balance, and updating the database!
            double newBalance = currentBalance - totalPrice;
            UserMapper.updateUserBalance(connectionPool, userId, newBalance);


            //Creating an order in database and retrieves the order id for future use!
            int orderId = OrderMapper.createOrder(connectionPool, userId);
            ctx.sessionAttribute("orderId", orderId);


            //Iterating through the basket, and saves each order line of the purchase in the database individually
            for (BasketItemDTO item : basket) {
                OrderDetails orderDetail = new OrderDetails(orderId, item.getBottomId(), item.getToppingId(), item.getQuantity(), item.getPrice());
                OrderMapper.saveOrderDetail(connectionPool, orderDetail);
            }

            //Retrieving all the individual lines of an order with a specific ID, and saves them in a List
            List<BasketItemDTO> orderDetails = OrderMapper.getOrderDetailsByOrderId(connectionPool, orderId);

            //Resetting session attribute "basket" to null
            ctx.sessionAttribute("basket", null);

            //Passing along attributes and render receipt
            ctx.attribute("orderId", orderId);
            ctx.attribute("orderDetails", orderDetails);

            ctx.render("receipt.html");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Kunne ikke gennemføre køb - prøv igen!");
            ctx.render("checkout.html");
        }
    }


    //Method for a basket overview
    private static void viewBasket(Context ctx, ConnectionPool connectionPool) {

        //Getting the List from session attributes
        List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
        //if statement to avoid null pointer exeception!
        if (basket == null) {
            basket = new ArrayList<>();
        }

        //Iterating through list to get the sum of prices
        double totalPrice = 0;

        for(BasketItemDTO order : basket) {
            totalPrice += order.getPrice();
        }

        //Passing along attributes and render next page!
        ctx.attribute("totalPrice", totalPrice);
        ctx.attribute("basket", basket);
        ctx.render("checkout.html");
    }

    //Method for actions following interaction with "basket" on homepage!
    private static void addToBasket(Context ctx, ConnectionPool connectionPool) {

        try {
            //Getting the List from session attributes
            List<BasketItemDTO> basket = ctx.sessionAttribute("basket");
            //if statement to avoid null pointer exeception!
            if (basket == null) {
                basket = new ArrayList<>();
            }

            //Retrieving parameters from front end form!
            String bottomName = ctx.formParam("bottom");
            String toppingName = ctx.formParam("topping");
            int quantity = Integer.parseInt(ctx.formParam("quantity"));

            //Retrieving information from database
            int bottomId = OrderMapper.getBottomId(connectionPool, bottomName);
            int toppingId = OrderMapper.getToppingId(connectionPool, toppingName);

            double bottomPrice = OrderMapper.getBottomPrice(connectionPool, bottomName);
            double toppingPrice = OrderMapper.getToppingPrice(connectionPool, toppingName);
            //Calculating totalprice
            double totalPrice = (bottomPrice + toppingPrice) * quantity;

            //Iterating through the items, adding the quantity and price, if it already exists in the basket
            boolean itemExists = false;
            for (BasketItemDTO item : basket) {
                if (item.getBottomId() == bottomId && item.getToppingId() == toppingId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    item.setPrice(item.getPrice() + totalPrice);
                    itemExists = true;
                    break;
                }
            }

            //Creating instances of our DTO and adding them to a list, if they dont already exist
            if (!itemExists) {
                BasketItemDTO basketItem = new BasketItemDTO(bottomId, bottomName, toppingId, toppingName, quantity, totalPrice);
                basket.add(basketItem);
            }


            //Passing along the list and keep the user on home page
            ctx.sessionAttribute("basket", basket);
            ctx.redirect("/");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Kunne ikke tilføje vare til indkøbskurv - prøv igen!");
            ctx.render("index.html");
        }
    }

    //Method for getting an overview of orders! Admin can see all orders, and users can only see its own orders, but this check happens in mapper function!
    private static void showAllOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            //Getting user information from session attributes
            Integer userId = ctx.sessionAttribute("userId");
            String role = ctx.sessionAttribute("role");

            //Confirming that the user is logged in, and if not an error message will appear
            if (userId == null) {
                ctx.attribute("errorMessage", "Du skal være logget ind for at se dine ordrer");
                ctx.render("index.html");
                return;
            }

            //Retrieving the order list from database, which contains admin check!
            List<UserAndOrderDTO> orderList = OrderMapper.getOrdersByRole(connectionPool, userId, role);

            //Passing along attributes and render next page!
            ctx.attribute("role", role);
            ctx.attribute("orderList", orderList);


            ctx.render("orders.html");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Kunne ikke vise dine ordrer!");
            ctx.render("index.html");
        }
    }
}
