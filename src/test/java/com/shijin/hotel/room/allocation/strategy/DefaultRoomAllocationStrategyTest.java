package com.shijin.hotel.room.allocation.strategy;

import com.shijin.hotel.room.allocation.model.AllocationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName( "DefaultRoomAllocationStrategy" )
class DefaultRoomAllocationStrategyTest {

   private DefaultRoomAllocationStrategy strategy;

   @BeforeEach
   void setUp() {
      strategy = new DefaultRoomAllocationStrategy();
   }

   private static final List<Double> DEFAULT_GUESTS = List.of( 23.0, 45.0, 155.0, 374.0, 22.0, 99.99, 100.0, 101.0, 115.0, 209.0 );

   @Nested
   @DisplayName( "Provided Test Cases" )
   class ProvidedTestCases {

      @Test
      @DisplayName( "Test 1: 3 Premium, 3 Economy rooms" )
      void testCase1_threePremiumThreeEconomy() {
         final AllocationResult result = strategy.allocate( 3, 3, DEFAULT_GUESTS );

         assertThat( result.premiumCount() ).isEqualTo( 3 );
         assertThat( result.premiumRevenue() ).isEqualTo( 738.0 );
         assertThat( result.economyCount() ).isEqualTo( 3 );
         assertThat( result.economyRevenue() ).isEqualTo( 167.99 );
      }

      @Test
      @DisplayName( "Test 2: 7 Premium, 5 Economy rooms" )
      void testCase2_sevenPremiumFiveEconomy() {
         final AllocationResult result = strategy.allocate( 7, 5, DEFAULT_GUESTS );

         assertThat( result.premiumCount() ).isEqualTo( 6 );
         assertThat( result.premiumRevenue() ).isEqualTo( 1054.0 );
         assertThat( result.economyCount() ).isEqualTo( 4 );
         assertThat( result.economyRevenue() ).isEqualTo( 189.99 );
      }

      @Test
      @DisplayName( "Test 3: 2 Premium, 7 Economy rooms" )
      void testCase3_twoPremiumSevenEconomy() {
         final AllocationResult result = strategy.allocate( 2, 7, DEFAULT_GUESTS );

         assertThat( result.premiumCount() ).isEqualTo( 2 );
         assertThat( result.premiumRevenue() ).isEqualTo( 583.0 );
         assertThat( result.economyCount() ).isEqualTo( 4 );
         assertThat( result.economyRevenue() ).isEqualTo( 189.99 );
      }
   }

   @Nested
   @DisplayName( "Edge Cases" )
   class EdgeCases {

      @Test
      @DisplayName( "No guests — empty list" )
      void noGuests_emptyList() {
         final AllocationResult result = strategy.allocate( 5, 5, Collections.emptyList() );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.premiumRevenue() ).isZero();
         assertThat( result.economyCount() ).isZero();
         assertThat( result.economyRevenue() ).isZero();
      }

      @Test
      @DisplayName( "No guests — null list" )
      void noGuests_nullList() {
         final AllocationResult result = strategy.allocate( 5, 5, null );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.premiumRevenue() ).isZero();
         assertThat( result.economyCount() ).isZero();
         assertThat( result.economyRevenue() ).isZero();
      }

      @Test
      @DisplayName( "No rooms available" )
      void noRoomsAvailable() {
         final AllocationResult result = strategy.allocate( 0, 0, DEFAULT_GUESTS );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.premiumRevenue() ).isZero();
         assertThat( result.economyCount() ).isZero();
         assertThat( result.economyRevenue() ).isZero();
      }

      @Test
      @DisplayName( "Only premium guests, no economy guests" )
      void onlyPremiumGuests() {
         final List<Double> guests = List.of( 200.0, 300.0, 150.0 );
         final AllocationResult result = strategy.allocate( 2, 3, guests );

         assertThat( result.premiumCount() ).isEqualTo( 2 );
         assertThat( result.premiumRevenue() ).isEqualTo( 500.0 );
         assertThat( result.economyCount() ).isZero();
         assertThat( result.economyRevenue() ).isZero();
      }

      @Test
      @DisplayName( "Only economy guests, no premium guests, economy rooms sufficient" )
      void onlyEconomyGuests_sufficientRooms() {
         final List<Double> guests = List.of( 30.0, 50.0, 70.0 );
         final AllocationResult result = strategy.allocate( 3, 3, guests );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.premiumRevenue() ).isZero();
         assertThat( result.economyCount() ).isEqualTo( 3 );
         assertThat( result.economyRevenue() ).isEqualTo( 150.0 );
      }

      @Test
      @DisplayName( "Only economy guests, excess upgraded to premium" )
      void onlyEconomyGuests_excessUpgraded() {
         final List<Double> guests = List.of( 30.0, 50.0, 70.0 );
         final AllocationResult result = strategy.allocate( 3, 2, guests );

         // 3 economy guests, 2 economy rooms → 1 excess, upgrade highest (70) to premium
         assertThat( result.premiumCount() ).isEqualTo( 1 );
         assertThat( result.premiumRevenue() ).isEqualTo( 70.0 );
         assertThat( result.economyCount() ).isEqualTo( 2 );
         assertThat( result.economyRevenue() ).isEqualTo( 80.0 ); // 50 + 30
      }

      @Test
      @DisplayName( "Exactly 100 EUR is treated as premium" )
      void exactly100IsPremium() {
         final List<Double> guests = List.of( 100.0 );
         final AllocationResult result = strategy.allocate( 1, 1, guests );

         assertThat( result.premiumCount() ).isEqualTo( 1 );
         assertThat( result.premiumRevenue() ).isEqualTo( 100.0 );
         assertThat( result.economyCount() ).isZero();
         assertThat( result.economyRevenue() ).isZero();
      }

      @Test
      @DisplayName( "99.99 EUR is treated as economy" )
      void just_below_100_is_economy() {
         final List<Double> guests = List.of( 99.99 );
         final AllocationResult result = strategy.allocate( 1, 1, guests );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.premiumRevenue() ).isZero();
         assertThat( result.economyCount() ).isEqualTo( 1 );
         assertThat( result.economyRevenue() ).isEqualTo( 99.99 );
      }
   }

   @Nested
   @DisplayName( "Smart Upgrade Logic" )
   class SmartUpgrade {

      @Test
      @DisplayName( "Economy guests upgraded when premium rooms available and economy full" )
      void upgradeEconomyGuestsWhenPremiumAvailable() {
         // 3 economy guests, only 1 economy room, 2 premium rooms free
         // Premium room: 0 premium guests. Upgrades: top 2 economy guests (90, 80)
         // Economy room: 1 economy guest (50)
         final List<Double> guests = List.of( 50.0, 80.0, 90.0 );
         final AllocationResult result = strategy.allocate( 2, 1, guests );

         assertThat( result.premiumCount() ).isEqualTo( 2 );
         assertThat( result.premiumRevenue() ).isEqualTo( 170.0 );
         assertThat( result.economyCount() ).isEqualTo( 1 );
         assertThat( result.economyRevenue() ).isEqualTo( 50.0 );
      }

      @Test
      @DisplayName( "No upgrade when economy rooms are sufficient" )
      void noUpgradeWhenEconomyRoomsSufficient() {
         final List<Double> guests = List.of( 50.0, 80.0 );
         final AllocationResult result = strategy.allocate( 2, 3, guests );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.premiumRevenue() ).isZero();
         assertThat( result.economyCount() ).isEqualTo( 2 );
         assertThat( result.economyRevenue() ).isEqualTo( 130.0 );
      }

      @Test
      @DisplayName( "No upgrade when no premium rooms remain" )
      void noUpgradeWhenNoPremiumRoomsRemain() {
         final List<Double> guests = List.of( 200.0, 150.0, 50.0, 80.0, 90.0 );
         final AllocationResult result = strategy.allocate( 2, 1, guests );

         // 2 premium guests fill 2 premium rooms — no room for upgrade
         assertThat( result.premiumCount() ).isEqualTo( 2 );
         assertThat( result.premiumRevenue() ).isEqualTo( 350.0 );
         assertThat( result.economyCount() ).isEqualTo( 1 );
         assertThat( result.economyRevenue() ).isEqualTo( 90.0 );
      }

      @Test
      @DisplayName( "Partial upgrade when fewer premium rooms than excess economy guests" )
      void partialUpgrade() {
         // 4 economy guests, 1 economy room -> 3 excess. Only 2 premium rooms free.
         final List<Double> guests = List.of( 10.0, 20.0, 30.0, 40.0 );
         final AllocationResult result = strategy.allocate( 2, 1, guests );

         // Upgrade top 2 economy (40, 30) to premium
         // Economy: top 1 remaining (20)
         assertThat( result.premiumCount() ).isEqualTo( 2 );
         assertThat( result.premiumRevenue() ).isEqualTo( 70.0 );
         assertThat( result.economyCount() ).isEqualTo( 1 );
         assertThat( result.economyRevenue() ).isEqualTo( 20.0 );
      }
   }

   @Nested
   @DisplayName( "Overbooking Scenarios" )
   class Overbooking {

      @Test
      @DisplayName( "More premium guests than premium rooms — highest payers get rooms" )
      void morePremiumGuestsThanRooms() {
         final List<Double> guests = List.of( 200.0, 300.0, 150.0, 400.0 );
         final AllocationResult result = strategy.allocate( 2, 0, guests );

         assertThat( result.premiumCount() ).isEqualTo( 2 );
         assertThat( result.premiumRevenue() ).isEqualTo( 700.0 ); // 400 + 300
         assertThat( result.economyCount() ).isZero();
      }

      @Test
      @DisplayName( "More economy guests than economy rooms — highest payers get rooms" )
      void moreEconomyGuestsThanRooms() {
         final List<Double> guests = List.of( 10.0, 50.0, 30.0, 70.0, 90.0 );
         final AllocationResult result = strategy.allocate( 0, 2, guests );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.economyCount() ).isEqualTo( 2 );
         assertThat( result.economyRevenue() ).isEqualTo( 160.0 ); // 90 + 70
      }
   }

   @Nested
   @DisplayName( "Single Guest Scenarios" )
   class SingleGuest {

      @Test
      @DisplayName( "Single premium guest with available room" )
      void singlePremiumGuest() {
         final AllocationResult result = strategy.allocate( 1, 1, List.of( 250.0 ) );

         assertThat( result.premiumCount() ).isEqualTo( 1 );
         assertThat( result.premiumRevenue() ).isEqualTo( 250.0 );
         assertThat( result.economyCount() ).isZero();
      }

      @Test
      @DisplayName( "Single economy guest with available room" )
      void singleEconomyGuest() {
         final AllocationResult result = strategy.allocate( 1, 1, List.of( 50.0 ) );

         assertThat( result.premiumCount() ).isZero();
         assertThat( result.economyCount() ).isEqualTo( 1 );
         assertThat( result.economyRevenue() ).isEqualTo( 50.0 );
      }
   }
}

