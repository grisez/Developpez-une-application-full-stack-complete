package com.openclassrooms.mddapi.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.openclassrooms.mddapi.security.AuthCookieManager;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Description of the API published by springdoc: general information and the authentication by cookie.
 */
@Configuration
public class OpenApiConfig {

	/** Name of the security scheme referenced by the protected endpoints. */
	public static final String COOKIE_AUTH = "cookieAuth";

	/**
	 * Builds the OpenAPI description. Every endpoint requires the authentication cookie unless it declares otherwise.
	 *
	 * @param buildProperties the build information written by Maven, which holds the version of the pom
	 * @return the OpenAPI description
	 */
	@Bean
	public OpenAPI mddOpenApi(ObjectProvider<BuildProperties> buildProperties) {
		// The build information only exists when Maven has built the project, not when it is run from the sources
		String version = buildProperties.stream().map(BuildProperties::getVersion).findFirst().orElse("dev");
		SecurityScheme cookieAuth = new SecurityScheme()
				.type(SecurityScheme.Type.APIKEY)
				.in(SecurityScheme.In.COOKIE)
				.name(AuthCookieManager.COOKIE_NAME)
				.description("JWT set in an HttpOnly cookie by the login and registration endpoints. "
						+ "State-changing requests must also send the XSRF-TOKEN cookie value in the X-XSRF-TOKEN header.");
		return new OpenAPI()
				.info(new Info()
						.title("MDD API")
						.version(version)
						.description("API of MDD (Monde de Dév), the social network for developers. "
								+ "Errors follow RFC 9457 (problem details)."))
				.components(new Components().addSecuritySchemes(COOKIE_AUTH, cookieAuth))
				.addSecurityItem(new SecurityRequirement().addList(COOKIE_AUTH));
	}

}
