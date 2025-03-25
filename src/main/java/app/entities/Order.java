package app.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Order {
    private int order_id;
    private int user_id;
    private LocalDateTime order_date;

    public Order(int order_id, int user_id, LocalDateTime order_date) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.order_date = order_date;
    }

    public Order(LocalDateTime order_date, int user_id) {
        this.user_id = user_id;
        this.order_date = order_date;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public LocalDateTime getOrder_date() {
        return order_date;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id=" + order_id +
                ", user_id=" + user_id +
                ", order_date=" + order_date +
                '}';
    }
}
