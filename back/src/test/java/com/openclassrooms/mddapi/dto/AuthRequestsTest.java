package com.openclassrooms.mddapi.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AuthRequestsTest {

	@Test
	void registerRequestTrimsEmailAndUsernameButKeepsThePasswordAsTyped() {
		// Act
		RegisterRequest request = new RegisterRequest("  alice@mdd.dev ", " alice ", " Str0ng!Password ");

		// Assert
		assertThat(request.email()).isEqualTo("alice@mdd.dev");
		assertThat(request.username()).isEqualTo("alice");
		assertThat(request.password()).isEqualTo(" Str0ng!Password ");
	}

	@Test
	void loginRequestTrimsTheIdentifierButKeepsThePasswordAsTyped() {
		// Act
		LoginRequest request = new LoginRequest(" alice ", " Str0ng!Password ");

		// Assert
		assertThat(request.identifier()).isEqualTo("alice");
		assertThat(request.password()).isEqualTo(" Str0ng!Password ");
	}

	@Test
	void updateProfileRequestTreatsAnEmptyPasswordAsUnchanged() {
		// Act
		UpdateProfileRequest request = new UpdateProfileRequest(" bob@mdd.dev ", " bob ", "");

		// Assert
		assertThat(request.email()).isEqualTo("bob@mdd.dev");
		assertThat(request.username()).isEqualTo("bob");
		assertThat(request.password()).isNull();
	}

	@Test
	void missingValuesStayNullSoThatValidationReportsThem() {
		// Act
		LoginRequest request = new LoginRequest(null, null);

		// Assert
		assertThat(request.identifier()).isNull();
	}

}
