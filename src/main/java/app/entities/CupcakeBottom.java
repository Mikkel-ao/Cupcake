package app.entities;

public class CupcakeBottom {
    private int bottom_id;
    private String bottom_name;
    private double price;

    public CupcakeBottom(int bottom_id, String bottom_name, double price) {
        this.bottom_id = bottom_id;
        this.bottom_name = bottom_name;
        this.price = price;
    }

    public CupcakeBottom(String bottom_name, double price) {
        this.bottom_name = bottom_name;
        this.price = price;
    }

    public int getBottom_id() {
        return bottom_id;
    }

    public String getBottom_name() {
        return bottom_name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "CupcakeBottom{" +
                "bottom_id=" + bottom_id +
                ", bottom_name='" + bottom_name + '\'' +
                ", price=" + price +
                '}';
    }
}
