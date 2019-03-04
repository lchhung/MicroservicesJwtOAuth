package com.github.dragonetail.oauth.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
public class JwtTokenProvider {
    private String jwtSecret;
    private int jwtExpirationInMs;

    public String generateToken(final Authentication authentication) {
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + this.jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
                .compact();
    }

    public Claims validateToken(final String authToken, final String clientIp) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.jwtSecret)
                    .parseClaimsJws(authToken)
                    .getBody();
        } catch (final SignatureException ex) {
            log.warn("[{}]JWT Signature verification failed: {}", clientIp, authToken);
        } catch (final MalformedJwtException ex) {
            log.warn("[{}]JWT token malformed: {}。", clientIp, authToken);
        } catch (final ExpiredJwtException ex) {
            log.warn("[{}]JWT token expired: {}", clientIp, authToken);
        } catch (final UnsupportedJwtException ex) {
            log.warn("[{}]JWT unsupported method: {}", clientIp, authToken);
        } catch (final IllegalArgumentException ex) {
            log.warn("[{}]JWT illegal argument: {}", clientIp, authToken);
        }
        return claims;
    }

    public static String JWT_HEADER_KEY = "Authorization";

    public String getJwtToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(JWT_HEADER_KEY);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public String buildJwtTokenForHeader(final Authentication authentication) {
        final String jwt = this.generateToken(authentication);
        return "Bearer " + jwt;
    }

}
