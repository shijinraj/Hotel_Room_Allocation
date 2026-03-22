package com.shijin.hotel.room.allocation.service;

import com.shijin.hotel.room.allocation.dto.OccupancyRequest;
import com.shijin.hotel.room.allocation.dto.OccupancyResponse;
import com.shijin.hotel.room.allocation.model.AllocationResult;
import com.shijin.hotel.room.allocation.strategy.RoomAllocationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
@DisplayName( "DefaultOccupancyService" )
class DefaultOccupancyServiceTest {

   @Mock
   private RoomAllocationStrategy allocationStrategy;

   @InjectMocks
   private DefaultOccupancyService service;

   @Test
   @DisplayName( "should delegate to allocation strategy and map result to response" )
   void calculateOccupancy_delegatesToStrategy() {
      final List<Double> guests = List.of( 100.0, 50.0 );
      final OccupancyRequest request = new OccupancyRequest( 3, 2, guests );

      final AllocationResult mockResult = new AllocationResult( 1, 100.0, 1, 50.0 );
      when( allocationStrategy.allocate( 3, 2, guests ) ).thenReturn( mockResult );

      final OccupancyResponse response = service.calculateOccupancy( request );

      assertThat( response.usagePremium() ).isEqualTo( 1 );
      assertThat( response.revenuePremium() ).isEqualTo( 100.0 );
      assertThat( response.usageEconomy() ).isEqualTo( 1 );
      assertThat( response.revenueEconomy() ).isEqualTo( 50.0 );

      verify( allocationStrategy ).allocate( 3, 2, guests );
   }

   @Test
   @DisplayName( "should handle zero rooms and empty guests" )
   void calculateOccupancy_zeroRoomsEmptyGuests() {
      final List<Double> guests = List.of();
      final OccupancyRequest request = new OccupancyRequest( 0, 0, guests );

      final AllocationResult mockResult = new AllocationResult( 0, 0.0, 0, 0.0 );
      when( allocationStrategy.allocate( 0, 0, guests ) ).thenReturn( mockResult );

      final OccupancyResponse response = service.calculateOccupancy( request );

      assertThat( response.usagePremium() ).isZero();
      assertThat( response.revenuePremium() ).isZero();
      assertThat( response.usageEconomy() ).isZero();
      assertThat( response.revenueEconomy() ).isZero();
   }
}

