package com.darkhex.hexalibre;

public class LoginService {

    // Placeholder for user login logic
    public void loginUser() {
        // Add Google OAuth sign-in logic here for User
    }

    // Placeholder for admin login logic
    public void loginAdmin() {
        // Add Google OAuth sign-in logic here for Admin
    }

    // A method to handle button click based on the type (user or admin)
    public void handleLoginButton(String userType) {
        if ("user".equals(userType)) {
            loginUser();
        } else if ("admin".equals(userType)) {
            loginAdmin();
        } else {
            System.out.println("Invalid user type.");
        }
    }
}
