package com.shijin.hotel.room.allocation;

import com.shijin.hotel.room.allocation.dto.OccupancyRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full integration tests — boots entire Spring context, exercises the real API end-to-end.
 * Validates all three test cases from the requirements.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName( "Occupancy API Integration Tests" )
class OccupancyIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper objectMapper;

   private static final List<Double> DEFAULT_GUESTS =
         List.of( 23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0 );

   @Test
   @DisplayName( "Test 1: 3 Premium, 3 Economy -> Premium: 3/738, Economy: 3/167.99" )
   void integrationTest1() throws Exception {
      final OccupancyRequest request = new OccupancyRequest( 3, 3, DEFAULT_GUESTS );

      mockMvc.perform( post( "/api/v1/occupancy" )
                  .contentType( MediaType.APPLICATION_JSON )
                  .content( objectMapper.writeValueAsString( request ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.usagePremium" ).value( 3 ) )
            .andExpect( jsonPath( "$.revenuePremium" ).value( 738.0 ) )
            .andExpect( jsonPath( "$.usageEconomy" ).value( 3 ) )
            .andExpect( jsonPath( "$.revenueEconomy" ).value( 167.99 ) );
   }

   @Test
   @DisplayName( "Test 2: 7 Premium, 5 Economy -> Premium: 6/1054, Economy: 4/189.99" )
   void integrationTest2() throws Exception {
      final OccupancyRequest request = new OccupancyRequest( 7, 5, DEFAULT_GUESTS );

      mockMvc.perform( post( "/api/v1/occupancy" )
                  .contentType( MediaType.APPLICATION_JSON )
                  .content( objectMapper.writeValueAsString( request ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.usagePremium" ).value( 6 ) )
            .andExpect( jsonPath( "$.revenuePremium" ).value( 1054.0 ) )
            .andExpect( jsonPath( "$.usageEconomy" ).value( 4 ) )
            .andExpect( jsonPath( "$.revenueEconomy" ).value( 189.99 ) );
   }

   @Test
   @DisplayName( "Test 3: 2 Premium, 7 Economy -> Premium: 2/583, Economy: 4/189.99" )
   void integrationTest3() throws Exception {
      final OccupancyRequest request = new OccupancyRequest( 2, 7, DEFAULT_GUESTS );

      mockMvc.perform( post( "/api/v1/occupancy" )
                  .contentType( MediaType.APPLICATION_JSON )
                  .content( objectMapper.writeValueAsString( request ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.usagePremium" ).value( 2 ) )
            .andExpect( jsonPath( "$.revenuePremium" ).value( 583.0 ) )
            .andExpect( jsonPath( "$.usageEconomy" ).value( 4 ) )
            .andExpect( jsonPath( "$.revenueEconomy" ).value( 189.99 ) );
   }

   @Test
   @DisplayName( "Empty guests list returns all zeros" )
   void emptyGuests() throws Exception {
      final OccupancyRequest request = new OccupancyRequest( 5, 5, List.of() );

      mockMvc.perform( post( "/api/v1/occupancy" )
                  .contentType( MediaType.APPLICATION_JSON )
                  .content( objectMapper.writeValueAsString( request ) ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.usagePremium" ).value( 0 ) )
            .andExpect( jsonPath( "$.revenuePremium" ).value( 0.0 ) )
            .andExpect( jsonPath( "$.usageEconomy" ).value( 0 ) )
            .andExpect( jsonPath( "$.revenueEconomy" ).value( 0.0 ) );
   }
}

