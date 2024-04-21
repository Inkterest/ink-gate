package init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import persistence.User;
import repository.UserRepository;
import type.Role;

@Component
public class UserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setEmail("ivoUser@mail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setRole(Role.USER);
        user.setFirstname("Ivo");
        userRepository.save(user);

        User userAdmin = new User();
        userAdmin.setEmail("ivoAdmin@mail.com");
        userAdmin.setPassword(passwordEncoder.encode("12345678"));
        userAdmin.setRole(Role.ADMIN);
        userAdmin.setFirstname("IvoAdmin");
        userRepository.save(userAdmin);

        User userSuperAdmin = new User();
        userSuperAdmin.setEmail("ivoSuperAdmin@mail.com");
        userSuperAdmin.setPassword(passwordEncoder.encode("12345678"));
        userSuperAdmin.setRole(Role.SUPERADMIN);
        userSuperAdmin.setFirstname("IvoSupperAdmin");
        userRepository.save(userSuperAdmin);

        user = new User();
        user.setEmail("nikUser@mail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setRole(Role.USER);
        user.setFirstname("Nik");
        user.setLastname("test 1");
        userRepository.save(user);

        userAdmin = new User();
        userAdmin.setEmail("nikAdmin@mail.com");
        userAdmin.setPassword(passwordEncoder.encode("12345678"));
        userAdmin.setRole(Role.ADMIN);
        userAdmin.setFirstname("NikAdmin");
        userAdmin.setLastname("test 2");
        userRepository.save(userAdmin);

        userSuperAdmin = new User();
        userSuperAdmin.setEmail("nikSuperAdmin@mail.com");
        userSuperAdmin.setPassword(passwordEncoder.encode("12345678"));
        userSuperAdmin.setRole(Role.SUPERADMIN);
        userSuperAdmin.setFirstname("NikSupperAdmin");
        userSuperAdmin.setLastname("test 3");
        userRepository.save(userSuperAdmin);

        user = new User();
        user.setEmail("michaelHunt@mail.com");
        user.setPassword(passwordEncoder.encode("2dxxv331fcasd"));
        user.setRole(Role.USER);
        user.setFirstname("Michael");
        user.setLastname("Hunt");
        userRepository.save(user);

        user = new User();
        user.setEmail("garryOak@mail.com");
        user.setPassword(passwordEncoder.encode("vadc2341?sd//13"));
        user.setRole(Role.USER);
        user.setFirstname("Garry");
        user.setLastname("Oak");
        userRepository.save(user);

        user = new User();
        user.setEmail("fancyFrank@mail.com");
        user.setPassword(passwordEncoder.encode("testing123"));
        user.setRole(Role.USER);
        user.setFirstname("Fancy");
        user.setLastname("Frank");
        userRepository.save(user);
    }
}
