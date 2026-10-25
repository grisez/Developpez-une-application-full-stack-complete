package com.openclassrooms.mddapi.security;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * Security of the API: stateless authentication by a JWT read from an HttpOnly cookie,
 * CSRF protection for the state-changing requests, and Argon2id password hashing.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

	/**
	 * The only routes reachable without being logged in. Logout is one of them so that a user whose token has
	 * already expired can still get rid of the cookie.
	 */
	private static final String[] PUBLIC_ENDPOINTS = { "/api/auth/register", "/api/auth/login", "/api/auth/logout" };

	/** API documentation, disabled in production by the springdoc properties of the prod profile. */
	private static final String[] API_DOCUMENTATION = { "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html" };

	/**
	 * Defines the security rules applied to every HTTP request.
	 *
	 * @param http                 the security builder
	 * @param jwtProperties        the JWT settings
	 * @param problemDetailHandler writes 401 and 403 errors as problem details
	 * @return the security filter chain
	 * @throws Exception if the configuration is invalid
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtProperties jwtProperties,
			ProblemDetailSecurityHandler problemDetailHandler) throws Exception {
		http
				// The built-in CSRF configuration is replaced by an explicit filter: the resource server would
				// otherwise skip CSRF checks for every request carrying a token, which is only safe for tokens sent
				// in the Authorization header, not for a cookie the browser attaches automatically.
				.csrf(AbstractHttpConfigurer::disable)
				.addFilterAt(csrfFilter(jwtProperties, problemDetailHandler), CsrfFilter.class)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers(HttpMethod.GET, API_DOCUMENTATION).permitAll()
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2
						.bearerTokenResolver(new CookieBearerTokenResolver(Set.of(PUBLIC_ENDPOINTS)))
						.jwt(Customizer.withDefaults())
						.authenticationEntryPoint(problemDetailHandler)
						.accessDeniedHandler(problemDetailHandler))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(problemDetailHandler)
						.accessDeniedHandler(problemDetailHandler));
		return http.build();
	}

	/**
	 * Writes security errors in the problem details format of the API.
	 *
	 * @param objectMapper the JSON mapper configured by Spring Boot
	 * @return the handler
	 */
	@Bean
	public ProblemDetailSecurityHandler problemDetailSecurityHandler(ObjectMapper objectMapper) {
		return new ProblemDetailSecurityHandler(objectMapper);
	}

	/**
	 * Signs the tokens with the HMAC secret.
	 *
	 * @param jwtProperties the JWT settings
	 * @return the encoder
	 */
	@Bean
	public JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(jwtProperties)));
	}

	/**
	 * Checks the signature, the expiration date and the issuer of every incoming token.
	 *
	 * @param jwtProperties the JWT settings
	 * @return the decoder
	 */
	@Bean
	public JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey(jwtProperties))
				.macAlgorithm(MacAlgorithm.HS256)
				.build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(JwtService.ISSUER));
		return decoder;
	}

	/**
	 * Hashes passwords with Argon2id, using the minimum parameters recommended by the OWASP Password Storage
	 * Cheat Sheet: 19 MiB of memory, 2 iterations, 1 degree of parallelism.
	 *
	 * @return the password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new Argon2PasswordEncoder(16, 32, 1, 19 * 1024, 2);
	}

	private CsrfFilter csrfFilter(JwtProperties jwtProperties, ProblemDetailSecurityHandler problemDetailHandler) {
		// Not HttpOnly on purpose: Angular must read this cookie to send its value back in the X-XSRF-TOKEN header
		CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
		repository.setCookieCustomizer(cookie -> cookie.sameSite("Strict").secure(jwtProperties.cookieSecure()));
		CsrfFilter filter = new CsrfFilter(repository);
		filter.setRequestHandler(new SpaCsrfTokenRequestHandler());
		filter.setAccessDeniedHandler(problemDetailHandler);
		return filter;
	}

	private SecretKey secretKey(JwtProperties jwtProperties) {
		return new SecretKeySpec(jwtProperties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}

}
