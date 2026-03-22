package com.shijin.hotel.room.allocation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI configuration.
 * <p>
 * Provides centralized API metadata exposed via {@code /api-docs}
 * and browsable through the Swagger UI at {@code /swagger-ui.html}.
 */
@Configuration
public class OpenApiConfig {

   @Bean
   public OpenAPI hotelAllocationOpenAPI() {
      return new OpenAPI()
            .info( new Info()
                  .title( "Hotel Room Allocation API" )
                  .description( "REST API for optimizing hotel room allocation between Premium and Economy categories." )
                  .version( "1.0.0" )
                  .contact( new Contact()
                        .name( "Hotel Allocation Team" )
                        .email( "shijinraj@gmail.com" ) )
                  .license( new License()
                        .name( "MIT License" )
                        .url( "https://opensource.org/licenses/MIT" ) ) );
   }
}

