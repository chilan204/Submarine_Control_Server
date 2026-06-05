package com.example.submarine_control_server;

import com.example.submarine_control_server.entities.Role;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.repositories.RoleRepository;
import com.example.submarine_control_server.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@RequiredArgsConstructor
public class SubmarineControlServerApplication implements CommandLineRunner {

    @Value("${app.init.admin.username:admin}")
    private String adminUsername;

    @Value("${app.init.admin.password:admin}")
    private String adminPassword;

    @Value("${app.init.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.init.admin.name:Administrator}")
    private String adminName;

    @Value("${app.init.admin.phone:000000000}")
    private String adminPhone;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SubmarineControlServerApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) {

        // =====================================================
        // INIT ROLES
        // =====================================================

        createRoleIfNotExists(
                "ADMIN",
                999
        );

        createRoleIfNotExists(
                "OFFICER_5",
                5
        );

        createRoleIfNotExists(
                "OFFICER_4",
                4
        );

        createRoleIfNotExists(
                "OFFICER_3",
                3
        );

        createRoleIfNotExists(
                "OFFICER_2",
                2
        );

        createRoleIfNotExists(
                "OFFICER_1",
                1
        );

        // =====================================================
        // INIT ADMIN USER
        // =====================================================

        Optional<User> existing =
                userRepository.findByUsername(adminUsername);

        if (existing.isPresent()) {

            System.out.println(
                    "Admin user already exists: "
                            + adminUsername
            );

            return;
        }

        if (userRepository.findByEmail(adminEmail).isPresent()) {

            System.out.println(
                    "Admin email already present in DB"
            );

            return;
        }

        Role adminRole =
                roleRepository.findByCode("ADMIN")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "ADMIN role not found"
                                )
                        );

        User admin = new User();

        admin.setUsername(
                adminUsername.toLowerCase()
        );

        admin.setName(adminName);

        admin.setEmail(
                adminEmail.toLowerCase()
        );

        admin.setPhone(adminPhone);

        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        admin.setCreatedBy("system");

        admin.setModifiedBy("system");

        admin.setRole(adminRole);

        userRepository.save(admin);

        System.out.println(
                "Default admin created: username="
                        + adminUsername
        );
    }

    // =====================================================
    // CREATE ROLE IF NOT EXISTS
    // =====================================================

    private void createRoleIfNotExists(String code, Integer priority) {

        boolean exists =
                roleRepository.findByCode(code).isPresent();

        if (exists) {
            return;
        }

        Role role = new Role();

        role.setCode(code);

        role.setPriority(priority);

        role.setCreatedBy("system");

        role.setModifiedBy("system");

        roleRepository.save(role);

        System.out.println(
                "Role created: " + code
        );
    }
}