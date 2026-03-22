package com.shijin.hotel.room.allocation.strategy;

import com.shijin.hotel.room.allocation.model.AllocationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Default implementation of the room allocation strategy.
 * <p>
 * Business rules:
 * <ul>
 *   <li>Premium guests (≥ 100 EUR) are only allocated to Premium rooms.</li>
 *   <li>Economy guests (< 100 EUR) are allocated to Economy rooms by default.</li>
 *   <li>Smart Upgrade: If Premium rooms are still available after all premium guests
 *       are allocated AND Economy rooms are full, the highest-paying economy guests
 *       get upgraded to Premium rooms.</li>
 *   <li>When there are more guests than rooms, only the highest-paying guests get rooms.</li>
 * </ul>
 */
@Component
public class DefaultRoomAllocationStrategy implements RoomAllocationStrategy {

   private static final double PREMIUM_THRESHOLD = 100.0;

   @Override
   public AllocationResult allocate( final int premiumRooms, final int economyRooms, final List<Double> potentialGuests ) {
      if ( potentialGuests == null || potentialGuests.isEmpty() ) {
         return new AllocationResult( 0, 0.0, 0, 0.0 );
      }

      // Partition guests into premium (>= 100) and economy (< 100), sorted descending
      final List<Double> premiumGuests = potentialGuests.stream()
            .filter( price -> price >= PREMIUM_THRESHOLD )
            .sorted( Comparator.reverseOrder() )
            .toList();

      final List<Double> economyGuests = potentialGuests.stream()
            .filter( price -> price < PREMIUM_THRESHOLD )
            .sorted( Comparator.reverseOrder() )
            .toList();

      // Allocate premium guests to premium rooms (highest payers first)
      final int premiumAllocated = Math.min( premiumGuests.size(), premiumRooms );
      final double premiumRevenue = premiumGuests.stream()
            .limit( premiumAllocated )
            .mapToDouble( Double::doubleValue )
            .sum();

      final int remainingPremiumRooms = premiumRooms - premiumAllocated;

      // Determine how many economy guests can be upgraded
      final int economyGuestCount = economyGuests.size();
      final int upgradableCount = calculateUpgradeCount( remainingPremiumRooms, economyRooms, economyGuestCount );

      // Upgrade the highest-paying economy guests to premium rooms
      final List<Double> upgradedGuests = new ArrayList<>( economyGuests.subList( 0, upgradableCount ) );
      final double upgradeRevenue = upgradedGuests.stream()
            .mapToDouble( Double::doubleValue )
            .sum();

      // Remaining economy guests after upgrades
      final List<Double> remainingEconomyGuests = economyGuests.subList( upgradableCount, economyGuestCount );

      // Allocate remaining economy guests to economy rooms
      final int economyAllocated = Math.min( remainingEconomyGuests.size(), economyRooms );
      final double economyRevenue = remainingEconomyGuests.stream()
            .limit( economyAllocated )
            .mapToDouble( Double::doubleValue )
            .sum();

      return new AllocationResult(
            premiumAllocated + upgradableCount,
            premiumRevenue + upgradeRevenue,
            economyAllocated,
            economyRevenue
      );
   }

   /**
    * Calculate how many economy guests should be upgraded to premium rooms.
    * Upgrades happen only when there are remaining premium rooms AND
    * economy guests exceed available economy rooms.
    */
   private int calculateUpgradeCount( final int remainingPremiumRooms, final int economyRooms, final int economyGuestCount ) {
      if ( remainingPremiumRooms <= 0 ) {
         return 0;
      }
      final int excessEconomyGuests = economyGuestCount - economyRooms;
      if ( excessEconomyGuests <= 0 ) {
         return 0;
      }
      return Math.min( remainingPremiumRooms, excessEconomyGuests );
   }
}

