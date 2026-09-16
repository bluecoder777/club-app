package au.jefrin.user.service;

import au.jefrin.auth.dto.LoginRequest;
import au.jefrin.auth.dto.RegistrationRequest;
import au.jefrin.common.exception.ConflictException;
import au.jefrin.common.exception.UnauthorizedException;
import au.jefrin.user.model.User;
import au.jefrin.user.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public void registerUser(RegistrationRequest request) throws SQLException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already registered");
        }

        // Hash the password securely using BCrypt
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));

        User user = new User(request.getName(), request.getEmail(), hashedPassword);
        userRepository.save(user);
    }

    public User authenticate(LoginRequest request) throws SQLException {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return user;
    }
}

