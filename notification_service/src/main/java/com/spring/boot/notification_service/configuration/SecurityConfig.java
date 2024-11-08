package com.spring.boot.notification_service.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
    //    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("http://localhost:4200");
//        configuration.addAllowedHeader("*");
//        configuration.addAllowedMethod("*");
//
//        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//    	@Bean
//        FirebaseMessaging firebaseMessaging() {
//
//		try {
//            log.info("Initializing FirebaseMessaging");
//			GoogleCredentials googleCredentials=GoogleCredentials.fromStream(
//					new ClassPathResource("accountFirebase.json").getInputStream());
//			FirebaseOptions firebaseOptions= FirebaseOptions.builder()
//					.setCredentials(googleCredentials)
//					.build();
//			FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions);
//            log.info("FirebaseApp initialized");
//			return FirebaseMessaging.getInstance(app);
//		}catch (IOException e){
//			throw new AppException(ErrorCode.FIREBASE_ERROR);
//		}
//
//	}
    private static final String[] PUBLIC_ENDPOINTS = {
            "/password/**",
            "/firebase/send_message",

    };

    private final CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("setting up filter chain");
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated());
//                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.disable());
        httpSecurity.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
        );
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
//        httpSecurity.csrf(AbstractHttpConfigurer::disable);
//        httpSecurity.csrf(AbstractHttpConfigurer::disable)
//                //...
//                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//                .authorizeHttpRequests(Customizer.withDefaults());
        httpSecurity.csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ws/**") // Vô hiệu hóa CSRF cho endpoint WebSocket
                );

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
