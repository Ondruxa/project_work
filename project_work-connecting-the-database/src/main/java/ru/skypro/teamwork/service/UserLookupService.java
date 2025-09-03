package ru.skypro.teamwork.service;

import org.springframework.stereotype.Service;
import ru.skypro.teamwork.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserLookupService {

    private final UserRepository userRepository;

    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserInfo> findByUsername(String usernameRaw) {
        return userRepository.findByUsername(usernameRaw)
                .map(r -> new UserInfo(r.id(), r.firstName(), r.lastName()));
    }

    public record UserInfo(UUID id, String firstName, String lastName) {
        public String fullName() {
            return firstName + " " + lastName;
        }
    }

    public Optional<UserInfo> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(r -> new UserInfo(r.id(), r.firstName(), r.lastName()));
    }
}