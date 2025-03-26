package app.DTO;

public class UserDTO {
    private int userId;
    private String email;
    private double balance;

    public UserDTO(int userId, String email, double balance) {
        this.userId = userId;
        this.email = email;
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                '}';
    }
}
