package com.shijin.hotel.room.allocation.controller;

import com.shijin.hotel.room.allocation.dto.OccupancyRequest;
import com.shijin.hotel.room.allocation.dto.OccupancyResponse;
import com.shijin.hotel.room.allocation.service.OccupancyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest( value = OccupancyController.class, properties = "spring.mvc.problemdetails.enabled=true" )
@DisplayName( "OccupancyController" )
class OccupancyControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private OccupancyService occupancyService;

   @MockitoBean
   private CacheManager cacheManager;

   @Autowired
   private ObjectMapper objectMapper;

   @Nested
   @DisplayName( "POST /api/v1/occupancy - Success" )
   class SuccessScenarios {

      @Test
      @DisplayName( "should return 200 with valid occupancy response and application/json content type" )
      void validRequest_returnsOk() throws Exception {
         final OccupancyRequest request = new OccupancyRequest( 7, 5,
               List.of( 23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0 ) );

         final OccupancyResponse response = new OccupancyResponse( 6, 1054.0, 4, 189.99 );
         when( occupancyService.calculateOccupancy( any( OccupancyRequest.class ) ) ).thenReturn( response );

         mockMvc.perform( post( "/api/v1/occupancy" )
                     .contentType( MediaType.APPLICATION_JSON )
                     .content( objectMapper.writeValueAsString( request ) ) )
               .andExpect( status().isOk() )
               .andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
               .andExpect( jsonPath( "$.usagePremium" ).value( 6 ) )
               .andExpect( jsonPath( "$.revenuePremium" ).value( 1054.0 ) )
               .andExpect( jsonPath( "$.usageEconomy" ).value( 4 ) )
               .andExpect( jsonPath( "$.revenueEconomy" ).value( 189.99 ) );
      }

      @Test
      @DisplayName( "should accept request with zero rooms" )
      void zeroRooms_returnsOk() throws Exception {
         final OccupancyRequest request = new OccupancyRequest( 0, 0, List.of( 50.0 ) );
         final OccupancyResponse response = new OccupancyResponse( 0, 0.0, 0, 0.0 );
         when( occupancyService.calculateOccupancy( any( OccupancyRequest.class ) ) ).thenReturn( response );

         mockMvc.perform( post( "/api/v1/occupancy" )
                     .contentType( MediaType.APPLICATION_JSON )
                     .content( objectMapper.writeValueAsString( request ) ) )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.usagePremium" ).value( 0 ) )
               .andExpect( jsonPath( "$.usageEconomy" ).value( 0 ) );
      }
   }

   @Nested
   @DisplayName( "POST /api/v1/occupancy - REST Contract Enforcement" )
   class RestContractEnforcement {

      @Test
      @DisplayName( "should return 415 Unsupported Media Type when Content-Type is not application/json" )
      void unsupportedMediaType_returns415() throws Exception {
         mockMvc.perform( post( "/api/v1/occupancy" )
                     .contentType( MediaType.APPLICATION_XML )
                     .content( "<request/>" ) )
               .andExpect( status().isUnsupportedMediaType() );
      }

      @Test
      @DisplayName( "should return 405 Method Not Allowed for GET /api/v1/occupancy" )
      void getMethodNotAllowed_returns405() throws Exception {
         mockMvc.perform( get( "/api/v1/occupancy" )
                     .accept( MediaType.APPLICATION_JSON ) )
               .andExpect( status().isMethodNotAllowed() );
      }
   }

   @Nested
   @DisplayName( "POST /api/v1/occupancy - Validation Errors (RFC 9457 ProblemDetail)" )
   class ValidationErrors {

      static Stream<Arguments> invalidRequestBodies() {
         return Stream.of(
               Arguments.of( "premiumRooms is null", """
                     {
                         "economyRooms": 5,
                         "potentialGuests": [23, 45]
                     }
                     """ ),
               Arguments.of( "economyRooms is null", """
                     {
                         "premiumRooms": 5,
                         "potentialGuests": [23, 45]
                     }
                     """ ),
               Arguments.of( "potentialGuests is null", """
                     {
                         "premiumRooms": 5,
                         "economyRooms": 3
                     }
                     """ ),
               Arguments.of( "premiumRooms is negative", """
                     {
                         "premiumRooms": -1,
                         "economyRooms": 5,
                         "potentialGuests": [23]
                     }
                     """ ),
               Arguments.of( "economyRooms is negative", """
                     {
                         "premiumRooms": 5,
                         "economyRooms": -1,
                         "potentialGuests": [23]
                     }
                     """ )
         );
      }

      @ParameterizedTest( name = "should return 400 ProblemDetail when {0}" )
      @MethodSource( "invalidRequestBodies" )
      void invalidRequest_returnsBadRequest( final String scenario, final String json ) throws Exception {
         mockMvc.perform( post( "/api/v1/occupancy" )
                     .contentType( MediaType.APPLICATION_JSON )
                     .content( json ) )
               .andExpect( status().isBadRequest() )
               .andExpect( content().contentType( MediaType.APPLICATION_PROBLEM_JSON ) )
               .andExpect( jsonPath( "$.title" ).value( "Bad Request" ) )
               .andExpect( jsonPath( "$.status" ).value( 400 ) )
               .andExpect( jsonPath( "$.detail" ).exists() )
               .andExpect( jsonPath( "$.instance" ).value( "/api/v1/occupancy" ) );
      }
   }
}

