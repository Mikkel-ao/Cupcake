package app.entities;

public class User {
private int user_id;
private String password;
private String email;
private String role;
private double balance;

    public User(int user_id, String password, String email, String role, double balance) {
        this.user_id = user_id;
        this.password = password;
        this.email = email;
        this.role = role;
        this.balance = balance;
    }

    public User(String password, String email, String role, double balance) {
        this.password = password;
        this.email = email;
        this.role = role;
        this.balance = balance;
    }

    public int getUser_id() {
        return user_id;
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

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", balance=" + balance +
                '}';
    }
}
