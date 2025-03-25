package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        //app.get("/", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/login",ctx -> login(ctx, connectionPool));
        app.get("/index", ctx -> ctx.render("index.html"));
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.post("/createuser",ctx -> createUser(ctx,connectionPool));

    }
    private static void login(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);

            ctx.sessionAttribute("currentUser",user.getUser_id());
            ctx.sessionAttribute("userId", user.getUser_id());
            ctx.sessionAttribute("role", user.getRole());
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
            ctx.render("/createuser.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "User already exists. Try again or log in.");
            ctx.render("/createuser.html");
        }
    }
}
