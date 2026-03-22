package com.shijin.hotel.room.allocation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request DTO for room occupancy calculation.
 *
 * @param premiumRooms    number of available premium rooms (must be >= 0)
 * @param economyRooms    number of available economy rooms (must be >= 0)
 * @param potentialGuests list of guest willingness-to-pay values (must not be null)
 */
@Schema( description = "Request payload for room occupancy calculation" )
public record OccupancyRequest(

      @Schema( description = "Number of available Premium rooms", example = "7", minimum = "0" )
      @NotNull( message = "premiumRooms must not be null" )
      @Min( value = 0, message = "premiumRooms must be >= 0" )
      Integer premiumRooms,

      @Schema( description = "Number of available Economy rooms", example = "5", minimum = "0" )
      @NotNull( message = "economyRooms must not be null" )
      @Min( value = 0, message = "economyRooms must be >= 0" )
      Integer economyRooms,

      @Schema( description = "List of guest willingness-to-pay values in EUR",
            example = "[23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209]" )
      @NotNull( message = "potentialGuests must not be null" )
      List<Double> potentialGuests
) {
}

