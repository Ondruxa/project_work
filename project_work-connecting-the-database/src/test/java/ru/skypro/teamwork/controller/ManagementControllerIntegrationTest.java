package ru.skypro.teamwork.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skypro.teamwork.repository.RecommendationsRepository;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ManagementControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private RecommendationsRepository recommendationsRepository;

    @Mock
    private BuildProperties buildProperties;

    @InjectMocks
    private ManagementController managementController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(managementController).build();
    }

    @Test
    void clearCaches_ShouldReturn204() throws Exception {
        mockMvc.perform(post("/management/clear-caches"))
                .andExpect(status().isNoContent());

        verify(recommendationsRepository, times(1)).clearCaches();
    }

    @Test
    void info_ShouldReturnJsonWithNameAndVersion() throws Exception {
        when(buildProperties.getName()).thenReturn("test-app");
        when(buildProperties.getVersion()).thenReturn("1.0.0");

        mockMvc.perform(get("/management/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test-app"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }
}
