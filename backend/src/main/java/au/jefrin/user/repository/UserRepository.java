package au.jefrin.user.repository;

import au.jefrin.user.model.User;

import java.sql.SQLException;

public interface UserRepository {
    boolean existsByEmail(String email) throws SQLException;

    User save(User user) throws SQLException;

    User findByEmail(String email) throws SQLException;
}
