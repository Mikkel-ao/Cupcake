package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;

public class Controller {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index.html"));

    }
}
