package ru.skypro.teamwork.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagementControllerTest {
    @Mock
    private RecommendationsRepository recommendationsRepository;

    @Mock
    private BuildProperties buildProperties;

    @InjectMocks
    private ManagementController managementController;

    @Test
    void clearCaches_ShouldCallRepositoryAndReturnNoContent() {
        // Act
        ResponseEntity<Void> response = managementController.clearCaches();

        // Assert
        verify(recommendationsRepository, times(1)).clearCaches();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void info_ShouldReturnNameAndVersionFromBuildProperties() {
        // Arrange
        String expectedName = "teamwork-service";
        String expectedVersion = "1.0.0";

        when(buildProperties.getName()).thenReturn(expectedName);
        when(buildProperties.getVersion()).thenReturn(expectedVersion);

        // Act
        Map<String, String> result = managementController.info();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedName, result.get("name"));
        assertEquals(expectedVersion, result.get("version"));

        verify(buildProperties, times(1)).getName();
        verify(buildProperties, times(1)).getVersion();
    }

    @Test
    void info_ShouldThrowNullPointerExceptionWhenNameIsNull() {
        // Arrange
        when(buildProperties.getName()).thenReturn(null);
        when(buildProperties.getVersion()).thenReturn("1.0.0");

        // Act & Assert
        assertThrows(NullPointerException.class, () -> managementController.info());
    }

    @Test
    void info_ShouldThrowNullPointerExceptionWhenVersionIsNull() {
        // Arrange
        when(buildProperties.getName()).thenReturn("teamwork-service");
        when(buildProperties.getVersion()).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> managementController.info());
    }

    @Test
    void info_ShouldThrowNullPointerExceptionWhenBothAreNull() {
        // Arrange
        when(buildProperties.getName()).thenReturn(null);
        when(buildProperties.getVersion()).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> managementController.info());
    }
}
