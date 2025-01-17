package cotato.growingpain.config;

import cotato.growingpain.security.RefreshTokenRepository;
import cotato.growingpain.security.jwt.JwtTokenProvider;
import cotato.growingpain.security.jwt.filter.JwtAuthenticationFilter;
import cotato.growingpain.security.jwt.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource;

    private final String[] WHITE_LIST = {
            "/swagger-ui/**",
            "/api/auth/**",
            "/v3/api-docs/**"
    };

    private final String[] REQUIRED_AUTHENTICATE = {
            "/api/post/**",
            "/api/comment/**",
            "/api/reply-comment/**",
            "/api/job-posts/**",
            "/api/member/**"
    };

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = sharedObject.build();
        http.authenticationManager(authenticationManager);

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(urlBasedCorsConfigurationSource))
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, refreshTokenRepository))
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(REQUIRED_AUTHENTICATE).hasAuthority("ROLE_MEMBER")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}