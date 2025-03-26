package app.entities;

import java.sql.Timestamp;

public class Order {

    private int orderId;
    private int userId;
    private Timestamp timestamp;

    public Order(int orderId, int userId, Timestamp timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public Order(int userId, Timestamp timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }


    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + orderId +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}
