package colcolat.javasinoweb.user.dto;

import colcolat.javasinoweb.user.model.User;
import  java.util.UUID;

public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private User.Role role;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.username = user.getUsername();
        response.email = user.getEmail();
        response.role = user.getRole();
        return response;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public User.Role getRole() {
        return role;
    }
}
