package printerBug;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class TokenCreater {

    // SVF Cloud Auth Endpoint
    private static final String TOKEN_URL = "https://api.svfcloud.com/oauth2/token";

    // default credentials (move here so other classes can call fetchAccessToken())
    private static final String DEFAULT_CLIENT_ID = "SVFFRVTBRUQNFUMCKMBNGLDASWDDRPZG";
    private static final String DEFAULT_SECRET_KEY = "KQ9uI7RdFmxhcWnnVbLUvOWeUczlN34DSNgeAYleo3iWDo8kgfX69ZWrFPczYzwz";
    private static final String DEFAULT_USER_ID = "KC01";
    private static final String DEFAULT_USER_NAME = "01クラフト";
    private static final String DEFAULT_TIMEZONE = "osaka";
    private static final String DEFAULT_LOCALE = "ja";
    private static final String DEFAULT_KEY_FILE = "C:\\Users\\CIS_SAGAR\\Downloads\\client.pem";

    public static void main(String[] args) throws Exception {
        // 🔴 YAHA HAJURKO CREDENTIALS HALNUHOS
        // use defaults (you can change these constants or call fetchAccessToken with different args)
        String clientId = DEFAULT_CLIENT_ID;
        String secretKey = DEFAULT_SECRET_KEY;
        String userId = DEFAULT_USER_ID;
        String userName = DEFAULT_USER_NAME;
        String timeZone = DEFAULT_TIMEZONE;
        String locale = DEFAULT_LOCALE;
        String keyFilePath = DEFAULT_KEY_FILE;

        // Load private key (supports PKCS#1 and PKCS#8 PEM using BouncyCastle)
        PrivateKey privateKey = loadPrivateKey(keyFilePath);
        System.out.println("Key Loaded Successfully!");


        
        
        
        /*
		 * System.out.println(Files.exists(Paths.get(keyFilePath)));
		 * 
		 * byte[] keyBytes = Files.readAllBytes(Paths.get(keyFilePath));
		 * PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes); KeyFactory kf =
		 * KeyFactory.getInstance("RSA"); PrivateKey privateKey =
		 * kf.generatePrivate(spec); System.out.println("Key Loaded Successfully!");
		 * 
		 */

        long exp = Instant.now().plusSeconds(3600).getEpochSecond();     // 1 hour
        String jwt = createJwt(clientId, userId, userName, timeZone, locale, exp, privateKey);

        System.out.println("Connecting to SVF Cloud to fetch Token...");
        String token = getAccessToken(clientId, secretKey, jwt);

        if (token != null) {
            System.out.println("\n🎉 SUCCESS! Your Access Token is:");
            System.out.println(token);
        } else {
            System.out.println("\n❌ FAILED! Could not retrieve token. Please check credentials or VPN connection.");
        }
    }

    // ---------------- JWT creation ----------------

    private static String createJwt(
            String clientId,
            String userId,
            String userName,
            String timeZone,
            String locale,
            long exp,
            PrivateKey privateKey) throws Exception {

        // Header: {"alg":"RS256"}
        String headerJson = "{\"alg\":\"RS256\"}";
        String headerB64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));

        // Claims
        StringBuilder claims = new StringBuilder();
        claims.append("{");
        claims.append("\"iss\":\"").append(clientId).append("\",");
        claims.append("\"sub\":\"").append(userId).append("\",");
        // exp should be a numeric value (seconds since epoch)
        claims.append("\"exp\":").append(exp).append(",");
        claims.append("\"userName\":\"").append(userName).append("\",");
        claims.append("\"timeZone\":\"").append(timeZone).append("\",");
        claims.append("\"locale\":\"").append(locale).append("\"");
        claims.append("}");

        String claimsB64 = base64UrlEncode(claims.toString().getBytes(StandardCharsets.UTF_8));

        String signingInput = headerB64 + "." + claimsB64;

        // Sign with SHA256withRSA
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(signingInput.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = sig.sign();

        String signatureB64 = base64UrlEncode(signatureBytes);

        // Final JWT
        return signingInput + "." + signatureB64;
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    /**
     * Load a private key from a PEM file. Supports PKCS#8 and PKCS#1 (RSA) PEMs.
     */
    private static PrivateKey loadPrivateKey(String keyFilePath) throws Exception {
        // register BC provider
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        try (Reader reader = Files.newBufferedReader(Paths.get(keyFilePath), StandardCharsets.UTF_8);
             PEMParser pemParser = new PEMParser(reader)) {
            Object obj = pemParser.readObject();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);

            if (obj instanceof PEMKeyPair) {
                return converter.getKeyPair((PEMKeyPair) obj).getPrivate();
            } else if (obj instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) obj);
            } else {
                throw new IllegalArgumentException("Unsupported PEM object: " + (obj == null ? "null" : obj.getClass()));
            }
        }
    }

    /**
     * Convenience method to fetch an access token using the DEFAULT_* credentials.
     * Other callers (like `SVF`) can call this to get the token programmatically.
     */
    public static String fetchAccessToken() throws Exception {
        PrivateKey privateKey = loadPrivateKey(DEFAULT_KEY_FILE);
        long exp = Instant.now().plusSeconds(3600).getEpochSecond();
        String jwt = createJwt(DEFAULT_CLIENT_ID, DEFAULT_USER_ID, DEFAULT_USER_NAME, DEFAULT_TIMEZONE, DEFAULT_LOCALE, exp, privateKey);
        System.out.println("Connecting to SVF Cloud to fetch Token...");
        return getAccessToken(DEFAULT_CLIENT_ID, DEFAULT_SECRET_KEY, jwt);
    }

    // ---------------- Token request ----------------

    public static String getAccessToken(String clientId, String secretKey, String jwtAssertion) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(TOKEN_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Authorization: Basic base64(clientId:secret)
            String basic = clientId + ":" + secretKey;
            String basicB64 = Base64.getEncoder()
                    .encodeToString(basic.getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + basicB64);

            conn.setDoOutput(true);

            // grant_type + assertion (JWT bearer)
            String postData =
                    "grant_type=" + URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", "UTF-8") +
                    "&assertion=" + URLEncoder.encode(jwtAssertion, "UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String json = response.toString();
                    // Response examples may use either "access_token" (OAuth2) or "token".
                    if (json.contains("\"access_token\"")) {
                        int start = json.indexOf("\"access_token\":\"") + 16;
                        int end = json.indexOf("\"", start);
                        return json.substring(start, end);
                    } else if (json.contains("\"token\"")) {
                        int start = json.indexOf("\"token\":\"") + 9;
                        int end = json.indexOf("\"", start);
                        return json.substring(start, end);
                    } else {
                        System.out.println("Unexpected response: " + json);
                    }
                }
            } else {
                System.out.println("API Error Code: [" + responseCode + "] " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            System.out.println("Network/Authentication Exception occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
