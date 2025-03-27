package app.controllers;

import app.DTO.UserDTO;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/login",ctx -> ctx.render("/login.html"));
        app.get("/index", ctx -> ctx.render("index.html"));
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.post("/createuser",ctx -> createUser(ctx,connectionPool));
        app.get("/customer", ctx -> showAllCustomers(ctx,connectionPool));
        app.get("/logout", ctx -> logout(ctx));
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);

            ctx.sessionAttribute("currentUser",user.getUserId());
            ctx.sessionAttribute("userId", user.getUserId());
            ctx.sessionAttribute("role", user.getRole());
            ctx.sessionAttribute("email", user.getEmail());
            ctx.redirect("/index");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Login failed. Please try again.");
            ctx.render("/login.html");
        }
    }
    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (!password1.equals(password2)) {
            ctx.attribute("message", "Passwords do not match, try again.");
            ctx.render("/createuser.html");
            return;
        }

        try {
            UserMapper.createUser(email, password1, connectionPool);
            ctx.attribute("message", "User created successfully. Please log in.");
            ctx.render("/login.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "User already exists. Try again or log in.");
            ctx.render("/createuser.html");
        }
    }
    private static void showAllCustomers(Context ctx, ConnectionPool connectionPool) {
        try {
            List<UserDTO> customersList = UserMapper.getAllUsers(connectionPool);
            ctx.attribute("customersList", customersList);
            ctx.render("customer.html");
        }catch (DatabaseException e) {
            ctx.status(500).result("An error occurred while fetching customers: " + e.getMessage());
        }
    }
}
