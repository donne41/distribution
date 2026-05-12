package example.authservice;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.UUID;

@Configuration
public class AutherizitionServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("gateway-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:5555/callback") //ändra till gateway
                .scope(OidcScopes.OPENID)
                .scope("user.read")
                .clientSettings(ClientSettings.builder().requireProofKey(false)
                        .build())
                .build();
        return new InMemoryRegisteredClientRepository(client);
    }


//    Problem att det inte kan välja ec kurvan, kan vara nått med att man testar på osäker sätt.
//    @Bean
//    public JWKSource<SecurityContext> jwkSource() throws JOSEException {
//        ECKey ecJwk = generateEc();
//
//        JWKSet jwkSet = new JWKSet(ecJwk);
//        return (selector, context) -> selector.select(jwkSet);
//    }
//
//    public static ECKey generateEc() {
//        try {
//            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
//            kpg.initialize(new ECGenParameterSpec("secp256r1"));
//            KeyPair keyPair = kpg.generateKeyPair();
//
//            ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
//            ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
//
//            return new ECKey.Builder(Curve.P_256, publicKey)
//                    .privateKey(privateKey)
//                    .keyID(UUID.randomUUID().toString())
//                    .algorithm(JWSAlgorithm.ES256) // viktigt ES256
//                    .build();
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//    }

    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Standard säkerhetsnivå
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
