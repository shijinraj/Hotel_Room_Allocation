package com.shijin.hotel.room.allocation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OpenApiConfig")
class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    @DisplayName("should create OpenAPI bean with correct metadata")
    void shouldCreateOpenApiBeanWithCorrectMetadata() {
        final OpenAPI openAPI = config.hotelAllocationOpenAPI();

        assertNotNull(openAPI);

        final Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Hotel Room Allocation API", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
        assertTrue(info.getDescription().contains("room allocation"));
        assertNotNull(info.getContact());
        assertEquals("Hotel Allocation Team", info.getContact().getName());
        assertEquals("shijinraj@gmail.com", info.getContact().getEmail());
        assertNotNull(info.getLicense());
        assertEquals("MIT License", info.getLicense().getName());
    }
}
