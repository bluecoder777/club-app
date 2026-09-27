package au.jefrin.user.repository;

import au.jefrin.user.model.User;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(String email);

    User save(User user);

    Optional<User> findByEmail(String email);
}
