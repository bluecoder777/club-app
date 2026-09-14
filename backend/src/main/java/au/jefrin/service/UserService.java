package au.jefrin.service;

import au.jefrin.dto.request.LoginRequest;
import au.jefrin.dto.request.RegistrationRequest;
import au.jefrin.model.User;
import au.jefrin.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public void registerUser(RegistrationRequest request) throws SQLException, IllegalArgumentException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Hash the password securely using BCrypt
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));

        User user = new User(request.getName(), request.getEmail(), hashedPassword);
        userRepository.save(user);
    }

    public User authenticate(LoginRequest request) throws SQLException, IllegalArgumentException {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }
}

