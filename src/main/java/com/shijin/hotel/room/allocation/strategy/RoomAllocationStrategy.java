package com.shijin.hotel.room.allocation.strategy;

import com.shijin.hotel.room.allocation.model.AllocationResult;

import java.util.List;

/**
 * Strategy interface for room allocation algorithms.
 * Follows the Strategy pattern (OCP — open for extension, closed for modification).
 */
public interface RoomAllocationStrategy {

   /**
    * Allocate guests to rooms based on the available room counts.
    *
    * @param premiumRooms    number of available premium rooms
    * @param economyRooms    number of available economy rooms
    * @param potentialGuests list of guest willingness-to-pay values
    * @return the allocation result with counts and revenues
    */
   AllocationResult allocate( int premiumRooms, int economyRooms, List<Double> potentialGuests );
}

