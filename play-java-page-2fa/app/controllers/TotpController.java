package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import utils.TotpUtil;

public class TotpController extends Controller {
    public Result generateSecret() {
        return ok(TotpUtil.generateSecret());
    }

    public Result generateQrCodeImage(String issuer, String application) {
        try {
            return ok(TotpUtil.generateQrCodeImage(issuer, application));
        } catch (Exception e) {
            return internalServerError("Failed to generate QR code image");
        }
    }
}
