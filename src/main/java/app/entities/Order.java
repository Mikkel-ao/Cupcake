package app.entities;

import java.sql.Timestamp;

public class Order {

    private int id;
    private int userId;
    private Timestamp timestamp;

    public Order(int id, int userId, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public Order(int userId, Timestamp timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }


    public int getId() {
        return id;
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
                "id=" + id +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}
