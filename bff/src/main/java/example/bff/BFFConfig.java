package example.bff;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
// import static org.springframework.web.servlet.function.RouterFunctions.route; Detta var fel route() metod.

@Configuration
public class BFFConfig {

    Logger LOG = LoggerFactory.getLogger(BFFConfig.class);


    @Bean
    SecurityFilterChain security(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated())
                // enable oauth2 login
                .oauth2Login(Customizer.withDefaults())
                //enable tokenRelay Oauth2 client
                .oauth2Client(Customizer.withDefaults())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> route1() {
        // /api/test -> http://localhost:8081/api/test

        return route()
                .GET("/api/test", http())
                .before( request -> {
                    LOG.info("Incoming request for route 1 to port 8081");
                    LOG.info("URI before: " + request.uri());
                    return request;
                })
                .before(uri("http://localhost:8081/"))
                // .before(setPath("/api/test")) Behövs inte för att uri är redan korrekt
                .before(request -> {
                            LOG.info("URI After: " + request.uri());
                            return request;
                        }
                )
                .filter(tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> route2(){
        // /api/test2 -> http://localhost:8082/api/test

        return route()
                .GET("/api/test2", http())
                .before( request -> {
                    LOG.info("Incoming request for route 2 to port 8082");
                    LOG.info("URI Before: " + request.uri());
                    return request;
                })
                .before(uri("http://localhost:8082/"))
                .before(setPath("/api/test"))
                .before(request -> {
                            LOG.info("URI After: " + request.uri());
                            return request;
                        }
                )
                .filter(tokenRelay())
                .build();
    }
}
