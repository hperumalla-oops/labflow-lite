package com.labflow.labflow_lite.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Dev seed: three users, password "password123". Replace with real onboarding in prod.
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;
        String hash = encoder.encode("password123");
        users.save(new User("tech1", hash, Role.TECHNICIAN));
        users.save(new User("reviewer1", hash, Role.REVIEWER));
        users.save(new User("reviewer2", hash, Role.REVIEWER));
    }
}