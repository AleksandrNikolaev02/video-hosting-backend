package com.example.auth_service.service;


import com.example.auth_service.model.UserAuthInfo;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.time.Instant;

public class JwtTokenProvider {
    @Value("${jwt.secret_key}")
    private String secretKey;
    @Value("${jwt.time-to-live-access-token}")
    private Integer timeToLiveToken;
    @Value("${jwt.issuer}")
    private String issuerToken;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public JwtTokenProvider(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(UserDetails user) {
        JwtClaimsSet claims = createJwtClaimsSet(user);

        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey.getBytes()));
        var params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        return encoder.encode(params).getTokenValue();
    }

    private JwtClaimsSet createJwtClaimsSet(UserDetails user) {
        return JwtClaimsSet.builder()
                .issuer(issuerToken)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(timeToLiveToken))
                .subject(user.getUsername())
                .claim("role", ((UserAuthInfo) user).getRole().getName().toString())
                .claim("id", ((UserAuthInfo) user).getId())
                .build();
    }

    public String getUsernameFromToken(String token) {
        try {
            var jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            return "";
        }
    }

    public String getRoleFromToken(String token) {
        var jwt = jwtDecoder.decode(token);

        return jwt.getClaimAsString("role");
    }

    public Integer getIdFromToken(String token) {
        try {
            var jwt = jwtDecoder.decode(token);
            return Integer.parseInt(jwt.getClaimAsString("id"));
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
