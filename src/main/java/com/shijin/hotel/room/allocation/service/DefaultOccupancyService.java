package com.shijin.hotel.room.allocation.service;

import com.shijin.hotel.room.allocation.dto.OccupancyRequest;
import com.shijin.hotel.room.allocation.dto.OccupancyResponse;
import com.shijin.hotel.room.allocation.model.AllocationResult;
import com.shijin.hotel.room.allocation.strategy.RoomAllocationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link OccupancyService}.
 * Delegates allocation logic to a {@link RoomAllocationStrategy} (DIP).
 * <p>
 * Results are cached using Spring's {@code @Cacheable} so that identical
 * requests avoid redundant computation.
 */
@Service
public class DefaultOccupancyService implements OccupancyService {

   private static final Logger log = LoggerFactory.getLogger( DefaultOccupancyService.class );

   private final RoomAllocationStrategy allocationStrategy;

   public DefaultOccupancyService( final RoomAllocationStrategy allocationStrategy ) {
      this.allocationStrategy = allocationStrategy;
   }

   @Cacheable( "calculateOccupancy" )
   @Override
   public OccupancyResponse calculateOccupancy( final OccupancyRequest request ) {
      log.info( "Calculating occupancy for {} premium rooms, {} economy rooms, {} guests",
            request.premiumRooms(), request.economyRooms(), request.potentialGuests().size() );

      final AllocationResult result = allocationStrategy.allocate(
            request.premiumRooms(),
            request.economyRooms(),
            request.potentialGuests()
      );

      log.info( "Allocation result: Premium {}/{}, Economy {}/{}",
            result.premiumCount(), request.premiumRooms(),
            result.economyCount(), request.economyRooms() );

      return new OccupancyResponse(
            result.premiumCount(),
            result.premiumRevenue(),
            result.economyCount(),
            result.economyRevenue()
      );
   }
}
