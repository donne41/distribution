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
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
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
                .oauth2Login(Customizer.withDefaults())
                //enable tokenRelay Oauth2 client
                .oauth2Client(Customizer.withDefaults())

                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                )
                // save csrf token for post request from diffrent modules
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
    public RouterFunction<ServerResponse> route1() {
        // /api/test -> http://localhost:8081/api/test

        return route()
                .GET("/api/test", http())
                .before(request -> {
                    LOG.info("Incoming request for route 1 to port 8081");
                    LOG.info("URI before: " + request.uri());
                    return request;
                })
                .before(uri("http://" + userServiceAdress + ":8081/"))
                // .before(setPath("/api/test")) Behövs inte för att uri är redan korrekt
                .before(request -> {
                            LOG.info("URI After: " + request.uri());
                            return request;
                        }
                )
                .filter(tokenRelay())
                .build();
    }


    // Wildcard Route --> matches all HTTP methods (GET,POST...) in Message Service
    @Bean
    public RouterFunction<ServerResponse> messageServiceRoute() {
        return route()
                .route(request -> request.uri().getPath().startsWith("/api/messages"), http())
                .before(uri("http://" + messageServiceAdress + ":8082"))
                .before(request -> {
                    LOG.info("Incoming {} request to Message Service", request.method());
                    return request;
                })
                .filter(tokenRelay())
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> routeToDashBoard() {
        return RouterFunctions.route()
                .GET("/", request -> {

                    // Get logged-in user from OAuth2
                    String username = request.principal().map(principal -> principal.getName())
                            .orElse("Guest");
                    return ServerResponse.ok()
                            .render("dashboard", java.util.Map.of("currentUsername", username));
                })
                .build();
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
        return oidcLogoutSuccessHandler;

    }
}
