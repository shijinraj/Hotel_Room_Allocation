package com.shijin.hotel.room.allocation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@DisplayName( "AllocationApplication" )
class AllocationApplicationTests {

	@Test
	@DisplayName( "Spring context loads successfully" )
	void contextLoads() {
	}

	@Test
	@DisplayName( "main method starts application without error" )
	void mainMethodStartsApplication() {
		assertThatCode( () -> AllocationApplication.main( new String[] {} ) )
				.doesNotThrowAnyException();
	}
}
