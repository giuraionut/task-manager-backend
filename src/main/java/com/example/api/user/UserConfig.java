//package com.example.api.user;
//
//import com.example.api.team.Team;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//import java.time.LocalDate;
//import java.time.ZonedDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//public class UserConfig {
//
//
//    @Bean
//    CommandLineRunner userRunner (UserRepository userRepository)
//    {
//        String email = "hetfield.mark@gmail.com";
//        return args -> {
//
//            User leader = new User(
//                    "mark01",
//                    "Hetfield",
//                    "Mark",
//                    "m",
//                    email,
//                    LocalDate.now().minusYears(20),
//                    ""
//            );
//            Team team = new Team(
//                    "team001",
//                     leader,
//                    null);
//
//            User member = new User(
//                    "anna43",
//                    "Jackson",
//                    "Anna",
//                    "f",
//                    email,
//                    LocalDate.now().minusYears(20),
//                    ""
//            );
//
//            userRepository.findUserByEmail(email).ifPresentOrElse(
//                    user -> { System.out.println("User exists"); },
//                    () ->  { System.out.println("User does not exists"); });
//        };
//
//    }
//
//}
