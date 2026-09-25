package au.jefrin.common.config;

import au.jefrin.auth.repository.JdbcRefreshTokenRepository;
import au.jefrin.auth.service.AuthService;
import au.jefrin.club.repository.JdbcClubRepository;
import au.jefrin.club.repository.JdbcDashboardPostRepository;
import au.jefrin.club.repository.JdbcMemberRepository;
import au.jefrin.club.service.ClubService;
import au.jefrin.club.service.DashboardService;
import au.jefrin.user.repository.JdbcUserRepository;
import au.jefrin.user.repository.UserRepository;
import au.jefrin.user.service.UserService;

public final class ServiceFactory {
    private ServiceFactory() {
    }

    public static AuthService createAuthService() {
        UserRepository userRepository = new JdbcUserRepository();
        UserService userService = new UserService(userRepository);
        return new AuthService(userService, userRepository, new JdbcRefreshTokenRepository());
    }

    public static ClubService createClubService() {
        return new ClubService(new JdbcClubRepository(), new JdbcMemberRepository());
    }

    public static DashboardService createDashboardService() {
        return new DashboardService(
                new JdbcDashboardPostRepository(),
                new JdbcMemberRepository(),
                new JdbcClubRepository()
        );
    }
}
