package com.fmcg.campaign.config;

import com.fmcg.campaign.entity.AppUser;
import com.fmcg.campaign.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedUsers(AppUserRepository appUserRepository) {
        return args -> {
            if (appUserRepository.count() > 0) {
                return;
            }

            AppUser user1 = new AppUser();
            user1.setId(101L);
            user1.setUserName("agent.ravi");
            user1.setContactNumber("+1-202-555-0101");

            AppUser user2 = new AppUser();
            user2.setId(102L);
            user2.setUserName("agent.maya");
            user2.setContactNumber("+1-202-555-0102");

            appUserRepository.save(user1);
            appUserRepository.save(user2);
        };
    }
}
