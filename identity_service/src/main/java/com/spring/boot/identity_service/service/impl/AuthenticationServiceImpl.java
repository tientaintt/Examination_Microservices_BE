package com.spring.boot.identity_service.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.spring.boot.identity_service.dto.request.AuthenticationRequest;
import com.spring.boot.identity_service.dto.request.IntrospectRequest;
import com.spring.boot.identity_service.dto.request.LogoutRequest;
import com.spring.boot.identity_service.dto.request.RefreshRequest;
import com.spring.boot.identity_service.dto.response.AuthenticationResponse;
import com.spring.boot.identity_service.dto.response.IntrospectResponse;
import com.spring.boot.identity_service.entity.InvalidatedToken;
import com.spring.boot.identity_service.entity.RefreshToken;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.exception.AppException;
import com.spring.boot.identity_service.exception.ErrorCode;
import com.spring.boot.identity_service.repository.InvalidatedTokenRepository;
import com.spring.boot.identity_service.repository.RefreshTokenRepository;
import com.spring.boot.identity_service.repository.UserRepository;
import com.spring.boot.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @NonFinal
    @Value("${jwt.access-token-expiration}")
    private long JWT_TOKEN_VALIDITY;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getLoginName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        log.info("{} +{}", request.getPassword(), user.getPassword());
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        log.info("Authenticated: {}", authenticated);

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(token.token)
                .expiryTime(token.expiryDate)
                .displayName(user.getDisplayName())
                .emailAddress(user.getEmailAddress())
                .roles(user.getRoles().stream().map(role -> role.getName()).toList())
                .build();
    }

    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(request.getToken());
        if (storedToken.isEmpty()) {
            log.info("No refresh token in database");
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_VALID);
        }
        if (!storedToken.get().getIsEnable()) {
            log.info("No refresh token in database");
//        refreshTokenRepository.deleteRefreshTokenBranch(storedToken.get().getId());
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_VALID);
        }
        if (storedToken.get().getExpiryDate().isBefore(Instant.now())) {
            log.info("refresh token expired");
            storedToken.get().setIsEnable(false);
            refreshTokenRepository.save(storedToken.get());
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_VALID);
        }

        SignedJWT signedJWT = null;
        try {
            signedJWT = verifyToken(request.getToken());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

//    String jit = null;
//    try {
//        jit = signedJWT.getJWTClaimsSet().getJWTID();
//    } catch (ParseException e) {
//        throw new RuntimeException(e);
//    }
//    Date expiryTime = null;
//    try {
//        expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//    } catch (ParseException e) {
//        throw new RuntimeException(e);
//    }

//    InvalidatedToken invalidatedToken =
//                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
//
//        invalidatedTokenRepository.save(invalidatedToken);

        String username = null;
        try {
            username = signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(token.token)
                .expiryTime(token.expiryDate)
                .build();
    }


    private TokenInfo generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        Date issueTime = new Date();
        Date expiryTime = new Date(Instant.ofEpochMilli(issueTime.getTime())
                .plus(JWT_TOKEN_VALIDITY, ChronoUnit.MINUTES)
                .toEpochMilli());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("exam.com")
                .issueTime(issueTime)
                .expirationTime(expiryTime)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("userId", user.getId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return new TokenInfo(jwsObject.serialize(), expiryTime);
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            invalidatedTokenRepository.save(InvalidatedToken.builder().id(token).expiryTime(expiryTime).build());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
//                if (!CollectionUtils.isEmpty(role.getPermissions()))
//                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    private record TokenInfo(String token, Date expiryDate) {

    }
}
