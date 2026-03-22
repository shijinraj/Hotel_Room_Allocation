package com.shijin.hotel.room.allocation.model;

/**
 * Immutable value object representing the result of a room allocation.
 *
 * @param premiumCount   number of premium rooms allocated
 * @param premiumRevenue total revenue from premium room allocations
 * @param economyCount   number of economy rooms allocated
 * @param economyRevenue total revenue from economy room allocations
 */
public record AllocationResult(
      int premiumCount,
      double premiumRevenue,
      int economyCount,
      double economyRevenue
) {
}

