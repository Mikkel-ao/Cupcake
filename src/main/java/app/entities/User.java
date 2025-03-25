package app.entities;

public class User {

    private String userId;
    private String password;
    private String email;
    private String role;
    private int balance;

    public User(String userId, String password, String email, String role, int balance) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.role = role;
        this.balance = balance;
    }

    public User(String password, String email, String role, int balance) {
        this.password = password;
        this.email = email;
        this.role = role;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", balance=" + balance +
                '}';
    }
}
