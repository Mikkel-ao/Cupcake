package app.entities;

public class CupcakeBottom {
    private int bottomId;
    private String bottomName;
    private double price;

    public CupcakeBottom(int bottomId, String bottomName, double price) {
        this.bottomId = bottomId;
        this.bottomName = bottomName;
        this.price = price;
    }

    public CupcakeBottom(String bottomName, double price) {
        this.bottomName = bottomName;
        this.price = price;
    }

    public int getBottomId() {
        return bottomId;
    }

    public String getBottomName() {
        return bottomName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "CupcakeBottom{" +
                "bottom_id=" + bottomId +
                ", bottom_name='" + bottomName + '\'' +
                ", price=" + price +
                '}';
    }
}
