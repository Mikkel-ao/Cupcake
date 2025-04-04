package app.DTO;

public class BasketItemDTO {

    private int bottomId;
    private String bottomName;
    private int toppingId;
    private String toppingName;
    private int quantity;
    private double price;

    public BasketItemDTO(int bottomId, String bottomName, int toppingId, String toppingName, int quantity, double price) {
        this.bottomId = bottomId;
        this.bottomName = bottomName;
        this.toppingId = toppingId;
        this.toppingName = toppingName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getBottomId() {
        return bottomId;
    }

    public String getBottomName() {
        return bottomName;
    }

    public int getToppingId() {
        return toppingId;
    }

    public String getToppingName() {
        return toppingName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setBottomId(int bottomId) {
        this.bottomId = bottomId;
    }

    public void setBottomName(String bottomName) {
        this.bottomName = bottomName;
    }

    public void setToppingId(int toppingId) {
        this.toppingId = toppingId;
    }

    public void setToppingName(String toppingName) {
        this.toppingName = toppingName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return quantity + " cupcake(s) with bottom " + bottomName + " and topping " + toppingName + " - Price: " + price;
    }
}
