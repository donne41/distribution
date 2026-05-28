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
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.security.Principal;
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

    @Value("${local.messageservice:localhost}")
    private String messageServiceAdress;

    @Bean
    SecurityFilterChain security(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/chatup/**", "/css/**", "/login/**").permitAll()
                        .requestMatchers("/", "/api/messages").authenticated()
                        .anyRequest().authenticated())
                // enable oauth2 login
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

                        // Tillåt tillfälligt PUT/POST-anrop till API-endpoints utan X-XSRF-TOKEN header för test i Insomnia
//                        .ignoringRequestMatchers("/api/**")
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
    public RouterFunction<ServerResponse> routeToSignupPage(){
        return route()
                .GET("/signup", http())
                .before(uri(userServiceAdress))
                .build();
    }



    // Wildcard Route --> matches all HTTP methods (GET,POST...) in Message Service
    @Bean
    public RouterFunction<ServerResponse> routePostCreateNewuser() {
        return route()
                .POST("/signup", http())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }

    public RouterFunction<ServerResponse> messageServiceRoute(){
        return route()
                .route(request -> request.uri().getPath().startsWith("/api/messages"), http())
                .before(uri(messageServiceAdress))
                .before(request -> {
                    LOG.info("Incoming {} request to Message Service", request.method());
                    return request;
                })
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
    public RouterFunction<ServerResponse> routeToDashBoard() {
        return RouterFunctions.route()
                .GET("/", request -> {
                    LOG.debug("-- GET / REQUEST --");
                    // Get logged-in user from OAuth2
                    String username = request.principal().map(principal -> principal.getName())
                            .orElse("Guest");
                    LOG.debug("-- USERNAME: {}", username);
                    return ServerResponse.ok()
                            .render("dashboard", java.util.Map.of("currentUsername", username));
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeToUpdateUser() {
        return route()
                .GET("/account", http())
                .before(request ->
                        request.principal()
                                .map(Principal::getName)
                                .map(username -> ServerRequest.from(request)
                                        .header("currentuser", username))
                                .get().build())
                .before(uri(userServiceAdress))
                .filter(tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeToSaveUpdate(){
        return route()
                .POST("/account", http())
                .before(request -> {
                    String token = request.cookies().getFirst("XSRF_TOKEN").getValue();
                    LOG.debug("CSRF TOKEN: "+ token);
                    return request;
                })
                .filter(tokenRelay())
                .before(uri(userServiceAdress))
                .build();
    }





}
