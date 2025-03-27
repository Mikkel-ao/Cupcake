package app.DTO;

import java.sql.Timestamp;

public class UserAndOrderDTO {

    private int orderId;
    private String email;
    private Timestamp orderDate;
    private double totalPrice;

    public UserAndOrderDTO(int orderId, String email, Timestamp orderDate, double totalPrice) {
        this.orderId = orderId;
        this.email = email;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "UserAndOrderDTO{" +
                "orderId=" + orderId +
                ", email='" + email + '\'' +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
