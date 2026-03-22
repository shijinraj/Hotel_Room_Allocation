package com.shijin.hotel.room.allocation.controller;

import com.shijin.hotel.room.allocation.dto.OccupancyRequest;
import com.shijin.hotel.room.allocation.dto.OccupancyResponse;
import com.shijin.hotel.room.allocation.service.OccupancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for room occupancy calculations.
 * <p>
 * Follows REST API design best practices:
 * <ul>
 *   <li>Explicit content negotiation via {@code produces} / {@code consumes}</li>
 *   <li>Thin controller — delegates all business logic to {@link OccupancyService}</li>
 *   <li>Bean Validation via {@code @Valid} for input correctness</li>
 *   <li>RFC 9457 ProblemDetail error responses (via Spring's built-in handler)</li>
 * </ul>
 */
@RestController
@RequestMapping( value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE )
@Tag( name = "Room Occupancy", description = "Operations for calculating hotel room occupancy and revenue" )
public class OccupancyController {

   private final OccupancyService occupancyService;

   public OccupancyController(final OccupancyService occupancyService ) {
      this.occupancyService = occupancyService;
   }

   /**
    * Calculate room occupancy and revenue.
    *
    * @param request the occupancy request with room counts and guest data
    * @return the occupancy response with usage and revenue
    */
   @Operation(
         summary = "Calculate room occupancy",
         description = """
               Optimizes room allocation between Premium (≥ EUR 100) and Economy (< EUR 100) categories.
               Guests paying ≥ EUR 100 are assigned to Premium rooms; those below to Economy rooms.
               When Economy rooms are full and Premium rooms remain, the highest-paying Economy guests
               are upgraded to Premium rooms."""
   )
   @ApiResponses( {
         @ApiResponse(
               responseCode = "200",
               description = "Allocation computed successfully",
               content = @Content(
                     mediaType = MediaType.APPLICATION_JSON_VALUE,
                     schema = @Schema( implementation = OccupancyResponse.class ),
                     examples = @ExampleObject( value = """
                           {
                               "usagePremium": 6,
                               "revenuePremium": 1054,
                               "usageEconomy": 4,
                               "revenueEconomy": 189.99
                           }""" )
               )
         ),
         @ApiResponse(
               responseCode = "400",
               description = "Validation failed — see RFC 9457 ProblemDetail body for details",
               content = @Content(
                     mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                     schema = @Schema( implementation = ProblemDetail.class )
               )
         )
   } )
   @PostMapping( value = "/occupancy", consumes = MediaType.APPLICATION_JSON_VALUE )
   public ResponseEntity<OccupancyResponse> calculateOccupancy( @Valid @RequestBody final OccupancyRequest request ) {
      return ResponseEntity.ok( occupancyService.calculateOccupancy( request ) );
   }
}

