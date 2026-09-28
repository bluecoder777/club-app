package au.jefrin.user.service;

import au.jefrin.auth.dto.RegistrationRequest;
import au.jefrin.common.exception.ConflictException;
import au.jefrin.user.model.User;
import au.jefrin.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    @Test
    void duplicateEmailProducesAStableBusinessErrorKey() {
        UserService service = new UserService(new DuplicateEmailUserRepository());
        RegistrationRequest request = new RegistrationRequest();
        request.setName("Existing User");
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.registerUser(request)
        );

        assertEquals("EMAIL_ALREADY_REGISTERED", exception.getErrorKey());
        assertEquals("Email is already registered", exception.getMessage());
    }

    private static class DuplicateEmailUserRepository implements UserRepository {
        @Override
        public boolean existsByEmail(String email) {
            return true;
        }

        @Override
        public User save(User user) {
            throw new AssertionError("Duplicate users must not be saved");
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.empty();
        }
    }
}
