package baedalmate.baedalmate.security.oauth2.service;

import baedalmate.baedalmate.security.oauth2.client.AppleClient;
import baedalmate.baedalmate.security.oauth2.dto.ApplePublicKeyResponse;
import baedalmate.baedalmate.security.oauth2.dto.AppleToken;
import baedalmate.baedalmate.security.oauth2.dto.AppleUserInfo;
import baedalmate.baedalmate.security.oauth2.dto.OAuth2UserInfo;
import baedalmate.baedalmate.security.oauth2.exception.ExpiredAccessTokenException;
import baedalmate.baedalmate.security.oauth2.exception.RsaNotFoundException;
import baedalmate.baedalmate.security.oauth2.soical.SocialType;
import baedalmate.baedalmate.security.oauth2.util.AppleJwtUtils;
import com.google.gson.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class AppleLoadStrategy {

    @Value("${apple.key.id}")
    private String keyId;

    @Value("${apple.team.id}")
    private String teamId;

    @Value("${apple.aud}")
    private String clientId;

    @Value("${apple.key.path}")
    private String keyPath;

    private final AppleClient appleClient;

    private final AppleJwtUtils appleJwtUtils;

    public OAuth2UserInfo getUserInfo(String identityToken, String authorizationCode, String username, String email)  {
        try {
            Claims userInfo = appleJwtUtils.getClaimsBy(identityToken);
            log.debug("Get userinfo");
            String clientSecret = makeClientSecret();
            log.debug("Make clientSecret");
            AppleToken.Response response = appleClient.getToken(AppleToken.Request.of(authorizationCode, clientId, clientSecret, "authorization_code", ""));
            log.debug("Get token");
            JsonParser parser = new JsonParser();
            JsonObject userInfoObject = (JsonObject) parser.parse(new Gson().toJson(userInfo));
            JsonElement appleAlg = userInfoObject.get("sub");
            String userId = appleAlg.getAsString();
            return new AppleUserInfo(username, email, userId);
        } catch (HttpClientErrorException | IOException e) {
            throw new ExpiredAccessTokenException();
        }
    }

    private String makeClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(keyPath);
        String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }
}
