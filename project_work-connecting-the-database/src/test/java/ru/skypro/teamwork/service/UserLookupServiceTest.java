package ru.skypro.teamwork.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.teamwork.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLookupServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserLookupService userLookupService;

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUserInfo() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String username = "test_user";
        String firstName = "John";
        String lastName = "Doe";

        UserRepository.UserRecord userRecord = new UserRepository.UserRecord(userId, firstName, lastName);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userRecord));

        // Act
        Optional<UserLookupService.UserInfo> result = userLookupService.findByUsername(username);

        // Assert
        assertThat(result).isPresent();
        UserLookupService.UserInfo userInfo = result.get();
        assertThat(userInfo.id()).isEqualTo(userId);
        assertThat(userInfo.firstName()).isEqualTo(firstName);
        assertThat(userInfo.lastName()).isEqualTo(lastName);
        assertThat(userInfo.fullName()).isEqualTo("John Doe");

        verify(userRepository).findByUsername(username);
    }

    @Test
    void findByUsername_WhenUserNotExists_ShouldReturnEmptyOptional() {
        // Arrange
        String username = "none_xistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<UserLookupService.UserInfo> result = userLookupService.findByUsername(username);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUsername(username);
    }

    @Test
    void findByUsername_WhenUsernameIsNull_ShouldReturnEmptyOptional() {
        // Arrange
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // Act
        Optional<UserLookupService.UserInfo> result = userLookupService.findByUsername(null);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUsername(null);
    }

    @Test
    void findByUsername_WhenUsernameIsBlank_ShouldReturnEmptyOptional() {
        // Arrange
        String blankUsername = "   ";
        when(userRepository.findByUsername(blankUsername)).thenReturn(Optional.empty());

        // Act
        Optional<UserLookupService.UserInfo> result = userLookupService.findByUsername(blankUsername);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findByUsername(blankUsername);
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUserInfo() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String firstName = "Jane";
        String lastName = "Smith";

        UserRepository.UserRecord userRecord = new UserRepository.UserRecord(userId, firstName, lastName);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userRecord));

        // Act
        Optional<UserLookupService.UserInfo> result = userLookupService.findById(userId);

        // Assert
        assertThat(result).isPresent();
        UserLookupService.UserInfo userInfo = result.get();
        assertThat(userInfo.id()).isEqualTo(userId);
        assertThat(userInfo.firstName()).isEqualTo(firstName);
        assertThat(userInfo.lastName()).isEqualTo(lastName);
        assertThat(userInfo.fullName()).isEqualTo("Jane Smith");

        verify(userRepository).findById(userId);
    }

    @Test
    void findById_WhenUserNotExists_ShouldReturnEmptyOptional() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserLookupService.UserInfo> result = userLookupService.findById(userId);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_WhenIdIsNull_ShouldReturnEmptyOptional() {
        // Arrange
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<UserLookupService.UserInfo> result = userLookupService.findById(null);

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findById(null);
    }

    @Test
    void userInfoFullName_ShouldReturnConcatenatedFirstNameAndLastName() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String firstName = "Alice";
        String lastName = "Johnson";

        // Act
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, firstName, lastName);

        // Assert
        assertThat(userInfo.fullName()).isEqualTo("Alice Johnson");
    }

    @Test
    void userInfoFullName_WhenFirstNameIsNull_ShouldHandleGracefully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String lastName = "Johnson";

        // Act
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, null, lastName);

        // Assert
        assertThat(userInfo.fullName()).isEqualTo("null Johnson");
    }

    @Test
    void userInfoFullName_WhenLastNameIsNull_ShouldHandleGracefully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String firstName = "Alice";

        // Act
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, firstName, null);

        // Assert
        assertThat(userInfo.fullName()).isEqualTo("Alice null");
    }

    @Test
    void userInfoFullName_WhenBothNamesAreNull_ShouldHandleGracefully() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, null, null);

        // Assert
        assertThat(userInfo.fullName()).isEqualTo("null null");
    }

    @Test
    void userInfoFullName_WhenNamesAreEmpty_ShouldHandleGracefully() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, "", "");

        // Assert
        assertThat(userInfo.fullName()).isEqualTo(" ");
    }

    @Test
    void userInfoRecord_ShouldHaveCorrectComponents() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String firstName = "Test";
        String lastName = "User";

        // Act
        UserLookupService.UserInfo userInfo = new UserLookupService.UserInfo(userId, firstName, lastName);

        // Assert
        assertThat(userInfo.id()).isEqualTo(userId);
        assertThat(userInfo.firstName()).isEqualTo(firstName);
        assertThat(userInfo.lastName()).isEqualTo(lastName);
    }
}
