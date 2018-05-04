package tools.jwt;

import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.annotation.PostConstruct;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;


/**
 * @author wangchunyang@gmail.com
 */
//@Component
public class TokenManager {
    public static final String USER_ID = "uid";
    public static final String WRAPPED_TOKEN = "wtk";

    public static final String ISSUER = "gateway";
    private static final String algorithm = "RSA"; // HMAC|RSA
    private byte[] sharedSecret; // only for MACSigner

    //@Inject
    private RSAKeyPairReader rsaKeyPairReader = new RSAKeyPairReader();

    private JWSVerifier userVerifier;
    private JWSSigner signer;
    private JWSVerifier verifier;

    //@PostConstruct
    public void init() {
        verifier = createVerifier();
        //userVerifier = createUserVerifier();

        // Generate random 256-bit (32 bytes) shared secret
        SecureRandom random = new SecureRandom();
        sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);
        signer = createSigner();
    }


    public boolean validateToken(String jwtToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            boolean verified = signedJWT.verify(userVerifier);
            if (verified) {
                Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
                return expirationTime.toInstant().isAfter(Instant.now());
            }
            return false;
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Failed to verify jwtToken: " + jwtToken, e);
        }
        //return false;
    }

    private JWSVerifier createVerifier() {
        return createRSAVerifier();
    }

//    private JWSVerifier createUserVerifier() {
//        return createUserRSAVerifier();
//    }

    private JWSVerifier createRSAVerifier() {
        return new RSASSAVerifier(rsaKeyPairReader.readPublicKey("keys/publicKey.der"));
    }

//    private JWSVerifier createUserRSAVerifier() {
//        return new RSASSAVerifier(rsaKeyPairReader.readPublicKey("keys/public.der"));
//    }

    public Token decodeToken(String jwtToken) {
        if (jwtToken == null) {
            return null;
        }
        Token token = new Token();
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            boolean verified = signedJWT.verify(verifier);
            token.setValid(verified);
            if (verified) {
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                //token.setUsername((String) claimsSet.getClaim(USERNAME));
                token.setUserId((String) claimsSet.getClaim(USER_ID));
                token.setWrappedToken((String) claimsSet.getClaim(WRAPPED_TOKEN));
                token.setExpirationTime(claimsSet.getExpirationTime());
            }
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Failed to verify token: " + jwtToken, e);
        }
        return token;
    }

    public String extractToken(String header) {
        if (header == null) {
            return null;
        }
        return header.length() > "Bearer ".length() ? header.substring("Bearer ".length()) : null;
    }

    public String generateToken(String userId, Date expiredDate) {
        try {
            // Prepare JWT with claims set
            // JWT time claim precision is seconds
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    //.claim(USERNAME, username)
                    .claim(USER_ID, userId)
                    //.claim(WRAPPED_TOKEN, wrappedToken)
                    .issuer(ISSUER)
                    .expirationTime(expiredDate)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader("RSA".equals(algorithm) ? JWSAlgorithm.RS256 : JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            // Serialize to compact form, produces something like
            // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("failed to generateToken for user: " + userId);
        }
    }

    private JWSSigner createSigner() {
        if ("RSA".equals(algorithm)) {
            return createRSASignerFromFile();
        }
        return createHMACSigner();
    }

    private JWSSigner createHMACSigner() {
        try {
            return new MACSigner(sharedSecret);
        } catch (KeyLengthException e) {
            throw new RuntimeException("failed to createHMACSigner.", e);
        }
    }

    private JWSSigner createRSASignerFromFile() {
        return new RSASSASigner(rsaKeyPairReader.readPrivateKey("keys/privateKey.der"));
    }

    public static void main(String[] args) {
        TokenManager tokenManager = new TokenManager();
        tokenManager.init();
        Instant expiredTime = Instant.now().plus(60, ChronoUnit.DAYS);
        String tokenString = tokenManager.generateToken("user01", Date.from(expiredTime));
        System.out.println(tokenString);

        Token token = tokenManager.decodeToken(tokenString);
        System.out.println(token);
    }
}
