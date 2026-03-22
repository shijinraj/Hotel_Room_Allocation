package com.shijin.hotel.room.allocation.service;

import com.shijin.hotel.room.allocation.dto.OccupancyRequest;
import com.shijin.hotel.room.allocation.dto.OccupancyResponse;

/**
 * Service interface for occupancy calculation.
 * Follows ISP — clients depend only on the methods they use.
 */
public interface OccupancyService {

   /**
    * Calculate room occupancy and revenue based on the given request.
    *
    * @param request the occupancy request containing room counts and guest data
    * @return the occupancy response with usage and revenue details
    */
   OccupancyResponse calculateOccupancy( OccupancyRequest request );
}

