package app.entities;

public class CupcakeTop {
    private int topping_id;
    private String topping_name;
    private double price;

    public CupcakeTop(int topping_id, String topping_name, double price) {
        this.topping_id = topping_id;
        this.topping_name = topping_name;
        this.price = price;
    }

    public CupcakeTop(String topping_name, double price) {
        this.topping_name = topping_name;
        this.price = price;
    }

    public int getTopping_id() {
        return topping_id;
    }

    public String getTopping_name() {
        return topping_name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "CupcakeTop{" +
                "topping_id=" + topping_id +
                ", topping_name='" + topping_name + '\'' +
                ", price=" + price +
                '}';
    }
}
