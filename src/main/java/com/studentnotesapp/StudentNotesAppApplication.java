package com.studentnotesapp;

import com.studentnotesapp.entity.Note;
import com.studentnotesapp.entity.User;
import com.studentnotesapp.repository.NoteRepository;
import com.studentnotesapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class StudentNotesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentNotesAppApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner sampleData(UserRepository userRepository, NoteRepository noteRepository, BCryptPasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("student").isEmpty()) {
                User defaultUser = new User();
                defaultUser.setUsername("student");
                defaultUser.setEmail("student@example.com");
                defaultUser.setPassword(encoder.encode("password123"));
                defaultUser.setRole("USER");
                userRepository.save(defaultUser);

                Note sample = new Note();
                sample.setTitle("Welcome to Student Notes");
                sample.setContent("This note was created automatically for the student user. Use the dashboard to add, edit, and delete your own notes.");
                sample.setUser(defaultUser);
                noteRepository.save(sample);
            }
        };
    }
}
