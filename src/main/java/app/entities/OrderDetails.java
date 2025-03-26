package app.entities;

//TODO: The name of the class should be in singularis!

public class OrderDetails {
    private int orderDetailId;
    private int orderId;
    private int bottomId;
    private int toppingId;
    private int quantity;
    private double cupcakePrice;

    public OrderDetails(int orderDetailId, int orderId, int bottomId, int toppingId, int quantity, double cupcakePrice) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.bottomId = bottomId;
        this.toppingId = toppingId;
        this.quantity = quantity;
        this.cupcakePrice = cupcakePrice;
    }

    public OrderDetails(int orderId, int bottomId, int toppingId, int quantity, double cupcakePrice) {
        this.orderId = orderId;
        this.bottomId = bottomId;
        this.toppingId = toppingId;
        this.quantity = quantity;
        this.cupcakePrice = cupcakePrice;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getBottomId() {
        return bottomId;
    }

    public int getToppingId() {
        return toppingId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCupcakePrice() {
        return cupcakePrice;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "order_detail_id=" + orderDetailId +
                ", order_id=" + orderId +
                ", bottom_id=" + bottomId +
                ", topping_id=" + toppingId +
                ", quantity=" + quantity +
                ", cupcake_price=" + cupcakePrice +
                '}';
    }
}
