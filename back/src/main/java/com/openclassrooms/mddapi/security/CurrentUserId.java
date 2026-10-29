package com.openclassrooms.mddapi.security;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * Injects the id of the logged-in user into a controller method: the subject of the JWT, read by Spring Security.
 * The id therefore always comes from the signed token, never from the request.
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@AuthenticationPrincipal(expression = "T(java.lang.Long).valueOf(subject)")
@Parameter(hidden = true)
public @interface CurrentUserId {
}
