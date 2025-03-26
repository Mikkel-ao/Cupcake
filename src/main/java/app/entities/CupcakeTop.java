package app.entities;

public class CupcakeTop {
    private int toppingId;
    private String toppingName;
    private double price;

    public CupcakeTop(int toppingId, String toppingName, double price) {
        this.toppingId = toppingId;
        this.toppingName = toppingName;
        this.price = price;
    }

    public CupcakeTop(String toppingName, double price) {
        this.toppingName = toppingName;
        this.price = price;
    }

    public int getToppingId() {
        return toppingId;
    }

    public String getToppingName() {
        return toppingName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "CupcakeTop{" +
                "topping_id=" + toppingId +
                ", topping_name='" + toppingName + '\'' +
                ", price=" + price +
                '}';
    }
}
