package app.entities;

//TODO: The name of the class should be in singularis!

public class OrderDetails {
    private int order_detail_id;
    private int order_id;
    private int bottom_id;
    private int topping_id;
    private int quantity;
    private double cupcake_price;

    public OrderDetails(int order_detail_id, int order_id, int bottom_id, int topping_id, int quantity, double cupcake_price) {
        this.order_detail_id = order_detail_id;
        this.order_id = order_id;
        this.bottom_id = bottom_id;
        this.topping_id = topping_id;
        this.quantity = quantity;
        this.cupcake_price = cupcake_price;
    }

    public OrderDetails(int order_id, int bottom_id, int topping_id, int quantity, double cupcake_price) {
        this.order_id = order_id;
        this.bottom_id = bottom_id;
        this.topping_id = topping_id;
        this.quantity = quantity;
        this.cupcake_price = cupcake_price;
    }

    public int getOrder_detail_id() {
        return order_detail_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getBottom_id() {
        return bottom_id;
    }

    public int getTopping_id() {
        return topping_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCupcake_price() {
        return cupcake_price;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "order_detail_id=" + order_detail_id +
                ", order_id=" + order_id +
                ", bottom_id=" + bottom_id +
                ", topping_id=" + topping_id +
                ", quantity=" + quantity +
                ", cupcake_price=" + cupcake_price +
                '}';
    }
}
