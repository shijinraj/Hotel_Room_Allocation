package com.shijin.hotel.room.allocation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for room occupancy calculation results.
 *
 * @param usagePremium   number of premium rooms occupied
 * @param revenuePremium total revenue from premium rooms
 * @param usageEconomy   number of economy rooms occupied
 * @param revenueEconomy total revenue from economy rooms
 */
@Schema( description = "Response payload with room occupancy usage and revenue" )
public record OccupancyResponse(
      @Schema( description = "Number of Premium rooms occupied", example = "6" )
      int usagePremium,
      @Schema( description = "Total revenue from Premium rooms in EUR", example = "1054" )
      double revenuePremium,
      @Schema( description = "Number of Economy rooms occupied", example = "4" )
      int usageEconomy,
      @Schema( description = "Total revenue from Economy rooms in EUR", example = "189.99" )
      double revenueEconomy
) {
}

