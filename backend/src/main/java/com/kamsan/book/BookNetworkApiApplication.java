package com.kamsan.book;

import com.kamsan.book.config.s3.S3Buckets;
import com.kamsan.book.config.s3.S3Service;
import com.kamsan.book.user.domain.Role;
import com.kamsan.book.user.enums.RoleType;
import com.kamsan.book.user.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookNetworkApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository, S3Service s3Service, S3Buckets s3Buckets) {
        return args -> {
            if (roleRepository.findByName(RoleType.ROLE_USER).isEmpty()) {
                roleRepository.save(Role.builder().name(RoleType.ROLE_USER).build());
            }
            if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build());
            }
            if (roleRepository.findByName(RoleType.ROLE_MANAGER).isEmpty()) {
                roleRepository.save(Role.builder().name(RoleType.ROLE_MANAGER).build());
            }
        };
    }
}
