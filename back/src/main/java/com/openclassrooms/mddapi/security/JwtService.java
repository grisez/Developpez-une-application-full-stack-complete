package com.openclassrooms.mddapi.security;

import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.User;

import lombok.RequiredArgsConstructor;

/**
 * Issues the signed JWT that identifies a logged-in user.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

	/** Issuer written in every token and required when a token is read back. */
	public static final String ISSUER = "mdd-api";

	private final JwtEncoder jwtEncoder;

	private final JwtProperties jwtProperties;

	/**
	 * Creates a token for the given user.
	 * The subject is the user id rather than the username, which the user can change from the profile page.
	 *
	 * @param user the authenticated user
	 * @return the signed token
	 */
	public String generateToken(User user) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(String.valueOf(user.getId()))
				.issuedAt(now)
				.expiresAt(now.plus(jwtProperties.expiration()))
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

}
