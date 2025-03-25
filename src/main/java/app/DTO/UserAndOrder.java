package app.DTO;

import java.sql.Timestamp;

public class UserAndOrder {

    private int orderId;
    private String email;
    private String bottomFlavor;
    private String topFlavor;
    private Timestamp orderDate;
    private int price;
    private int quantity;

    public UserAndOrder(int orderId, String email, String bottomFlavor, String topFlavor, Timestamp orderDate, int price, int quantity) {
        this.orderId = orderId;
        this.email = email;
        this.bottomFlavor = bottomFlavor;
        this.topFlavor = topFlavor;
        this.orderDate = orderDate;
        this.price = price;
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getEmail() {
        return email;
    }

    public String getBottomFlavor() {
        return bottomFlavor;
    }

    public String getTopFlavor() {
        return topFlavor;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "UserAndOrder{" +
                "orderId=" + orderId +
                ", email='" + email + '\'' +
                ", bottomFlavor='" + bottomFlavor + '\'' +
                ", topFlavor='" + topFlavor + '\'' +
                ", orderDate=" + orderDate +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
