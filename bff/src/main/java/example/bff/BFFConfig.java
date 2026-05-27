package example.bff;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.*;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.redirectTo;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
// import static org.springframework.web.servlet.function.RouterFunctions.route; Detta var fel route() metod.

@Configuration
public class BFFConfig {

    Logger LOG = LoggerFactory.getLogger(BFFConfig.class);
    @Value("${local.userservice}")
    private String userServiceAdress;

    @Bean
    SecurityFilterChain security(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/chatup/**", "/css/**", "/login/**", "/signup").permitAll()
                        .requestMatchers("/", "/api/test2").authenticated()
                        .anyRequest().authenticated())
                // enable oauth2 login

                .oauth2Login(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/", true))

                //enable tokenRelay Oauth2 client
                .oauth2Client(Customizer.withDefaults())

                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                )
//                 save csrf token for post request from diffrent modules
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .build();
    }


    //csrf token redering before proxy to service
    @Bean
    public OncePerRequestFilter csrfCookieFilter(){
        return new OncePerRequestFilter(){
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
                CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if(token != null){
                    token.getToken();
                }
                filterChain.doFilter(request,response);
            }
        };
    }


    @Bean
    public RouterFunction<ServerResponse> routeToDashBoard() {
        return RouterFunctions.route()
                .GET("/", request -> {
                    return ServerResponse.ok()
                            .render("dashboard");
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeToSignupPage(){
        return route()
                .GET("/signup", http())
                .before(uri(userServiceAdress))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routePostCreateNewuser(){
        return route()
                .POST("/signup", http())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }

    // Flytta till Authservice
    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
        return oidcLogoutSuccessHandler;

    }

    @Bean
    public RouterFunction<ServerResponse> routeForGettingToken(){
        return route()
                .GET("/get/token", http())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> routeGetAllUsers(){
        return route()
                .GET("/get/users", http())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routePostNewuser(){
        return route()
                .POST("/get/users", http())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeGetUserDetail(){
        return route()
                .GET("/api/users", http())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeToFindUser(){
        return route()
                .POST("/find/**", http())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }






}
