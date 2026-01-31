package services;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import com.typesafe.config.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TotpService {
    private final String secret;

    @Inject
    // 3. Ensure the constructor uses the Java play.Configuration type
    public TotpService(Config config) {
        this.secret = config.getString("totp.secret");
        if (this.secret == null || this.secret.isBlank()) {
            throw new IllegalStateException("TOTP secret is not configured in application.conf");
        }
    }

    public boolean verifyCode(String code) {
        // The TimeProvider class is now imported and recognized
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setAllowedTimePeriodDiscrepancy(2);
        return verifier.isValidCode(this.secret, code);
    }
}