package app.DTO;

public class BasketOrder {

    private String bottom;
    private String topping;
    private int quantity;
    private double price;

    public BasketOrder(String bottom, String topping, int quantity, double price) {
        this.bottom = bottom;
        this.topping = topping;
        this.quantity = quantity;
        this.price = price;
    }

    public String getBottom() {
        return bottom;
    }

    public String getTopping() {
        return topping;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return quantity + " cupcake(s) with bottom " + bottom + " and topping " + topping + " - Price: " + price;
    }
}
