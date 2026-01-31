package utils;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;

public class TotpUtil {
    public static String generateSecret() {
        // 1. Generate a new secret
        var secretGenerator = new DefaultSecretGenerator();
        return secretGenerator.generate();
    }

    public static String generateQrCodeImage(String issuer, String application) throws  Exception {
        var secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();

        // 2. Generate the QR code URI for easy setup in an authenticator app
        var qrData = new QrData.Builder()
                .label(application) // A label for the authenticator app
                .secret(secret)
                .issuer(issuer) // Your application's name
                .algorithm(HashingAlgorithm.SHA1) // SHA1 is standard
                .digits(6)
                .period(30)
                .build();

        return qrData.getUri();
    }
}
