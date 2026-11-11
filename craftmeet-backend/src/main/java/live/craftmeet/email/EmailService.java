package live.craftmeet.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

/**
 * Service responsible for sending transactional emails through SendGrid.
 * Provides the email-verification and password-reset templates used by the
 * authentication flows, matching the shared Craftmeet branding and layout.
 */
@Slf4j
@Service
public class EmailService {

    private static final String FONT_STACK = "'Figtree','Figtree Variable',-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif";

    private static final String LOGO_SVG = """
            <svg width="34" height="34" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Craftmeet" style="display:block;"><path fill-rule="evenodd" fill="#005153" d="M 18 5 L 76 5 Q 88 5 88 17 L 88 61 Q 88 73 76 73 L 36 73 L 19 90 L 25 73 L 18 73 Q 6 73 6 61 L 6 17 Q 6 5 18 5 Z M 18 14 L 76 14 Q 80 14 80 18 L 80 32 Q 80 36 76 36 L 18 36 Q 14 36 14 32 L 14 18 Q 14 14 18 14 Z M 18 41 L 40 41 Q 44 41 44 45 L 44 63 Q 44 67 40 67 L 18 67 Q 14 67 14 63 L 14 45 Q 14 41 18 41 Z M 54 41 L 76 41 Q 80 41 80 45 L 80 63 Q 80 67 76 67 L 54 67 Q 50 67 50 63 L 50 45 Q 50 41 54 41 Z M 65 48 L 67 52 L 71 54 L 67 56 L 65 60 L 63 56 L 59 54 L 63 52 Z"/></svg>""";

    private final Environment environment;
    private final String sendgridApiKey;
    private final String sendgridFromEmail;
    private final String sendgridFromName;
    private final long verifyTokenExpiration;
    private final long resetTokenExpiration;

    public EmailService(Environment environment, @Value("${sendgrid.api-key}") String sendgridApiKey,
            @Value("${sendgrid.from-email}") String sendgridFromEmail,
            @Value("${sendgrid.from-name}") String sendgridFromName,
            @Value("${verify.token.expiration}") long verifyTokenExpiration,
            @Value("${reset.token.expiration}") long resetTokenExpiration) {
        this.environment = environment;
        this.sendgridApiKey = sendgridApiKey;
        this.sendgridFromEmail = sendgridFromEmail;
        this.sendgridFromName = sendgridFromName;
        this.verifyTokenExpiration = verifyTokenExpiration;
        this.resetTokenExpiration = resetTokenExpiration;
    }

    /**
     * Sends the email-verification message to the given address.
     *
     * @param toEmail          the recipient's email address
     * @param verificationLink the verification link to embed in the email
     */
    public void sendVerificationEmail(String toEmail, String token) {
        long expiryHours = verifyTokenExpiration / 3_600_000L;
        String html = buildVerificationHtml(buildVerificationLink(token), expiryHours);
        send(toEmail, "Verify your Craftmeet account", html);
    }

    /**
     * Sends the password-reset message to the given address.
     *
     * @param toEmail   the recipient's email address
     * @param resetLink the password reset link to embed in the email
     */
    public void sendResetPasswordEmail(String toEmail, String token) {
        long expiryMinutes = resetTokenExpiration / 60_000L;
        String html = buildResetPasswordHtml(buildVerificationLink(token), expiryMinutes);
        send(toEmail, "Reset your Craftmeet password", html);
    }

    private void send(String toEmail, String subject, String htmlContent) {
        Email from = new Email(sendgridFromEmail, sendgridFromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        try {
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new SendEmailException("Failed to send email to " + toEmail
                        + ": status " + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Failed to send email to {}", toEmail, e);
            throw new SendEmailException("Failed to send email to " + toEmail);
        }
    }

    /**
     * Builds the email-verification link for the given token, using the
     * environment-appropriate frontend base URL.
     *
     * @param token the raw verification token to embed in the link
     * @return the full verification URL
     */
    public String buildVerificationLink(String token) {
        String baseUrl = isDev() ? "http://localhost:5173" : "https://craftmeet.live";
        return baseUrl + "/verify?token=" + token;
    }

    /**
     * Builds the password-reset link for the given token, using the
     * environment-appropriate frontend base URL.
     *
     * @param token the raw reset token to embed in the link
     * @return the full password reset URL
     */
    public String buildResetPasswordLink(String token) {
        String baseUrl = isDev() ? "http://localhost:5173" : "https://craftmeet.live";
        return baseUrl + "/reset-password?token=" + token;
    }

    private String buildVerificationHtml(String emailLink, long expiryHours) {
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <meta name="color-scheme" content="light only">
                  <meta name="supported-color-schemes" content="light">
                  <title>Verify your Craftmeet account</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f5f7f9;-webkit-text-size-adjust:100%;">
                  <div style="display:none;max-height:0;overflow:hidden;opacity:0;font-size:1px;line-height:1px;color:#f5f7f9;">
                    Confirm your email address to activate your Craftmeet account.&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;
                  </div>
                  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f5f7f9;">
                    <tr>
                      <td align="center" style="padding:40px 20px;">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:100%;max-width:600px;">
                          <tr>
                            <td align="center" style="padding:0 0 24px 0;">
                              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                  <td style="vertical-align:middle;padding-right:10px;">{logo_svg}</td>
                                  <td style="vertical-align:middle;font-family:{font_stack};font-size:22px;font-weight:700;letter-spacing:-0.02em;color:#005153;">Craftmeet</td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr>
                            <td style="background-color:#ffffff;border:1px solid #d8dbdf;border-radius:16px;padding:44px 40px;">
                              <h1 style="margin:0 0 10px 0;font-family:{font_stack};font-size:22px;line-height:1.3;font-weight:600;letter-spacing:-0.02em;color:#0f1417;">Verify your email address</h1>
                              <p style="margin:0 0 28px 0;font-family:{font_stack};font-size:15px;line-height:1.6;color:#6d7277;">Thanks for signing up. Confirm your email address to activate your account and start running better meetings.</p>
                              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                  <td align="center" style="border-radius:10px;background-color:#005153;">
                                    <a href="{email_link}" target="_blank" style="display:inline-block;padding:13px 30px;font-family:{font_stack};font-size:15px;font-weight:600;line-height:1;color:#ffffff;text-decoration:none;border-radius:10px;">Verify my account</a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:28px 0 0 0;font-family:{font_stack};font-size:13px;line-height:1.6;color:#6d7277;">This link expires in <strong style="color:#0f1417;">{expiry_hours} hours</strong>. If the button doesn't work, copy and paste this URL into your browser:</p>
                              <p style="margin:8px 0 0 0;font-family:{font_stack};font-size:13px;line-height:1.6;word-break:break-all;">
                                <a href="{email_link}" target="_blank" style="color:#005153;text-decoration:underline;">{email_link}</a>
                              </p>
                              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0">
                                <tr><td style="padding:28px 0 0 0;border-top:1px solid #d8dbdf;"></td></tr>
                              </table>
                              <p style="margin:24px 0 0 0;font-family:{font_stack};font-size:12px;line-height:1.6;color:#6d7277;">If you didn't create a Craftmeet account, you can safely ignore this email.</p>
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding:24px 0 0 0;font-family:{font_stack};font-size:12px;line-height:1.6;color:#6d7277;">
                              &copy; Craftmeet &middot; Real-time structured meetings
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """;
        return html
                .replace("{font_stack}", FONT_STACK)
                .replace("{logo_svg}", LOGO_SVG)
                .replace("{email_link}", emailLink)
                .replace("{expiry_hours}", String.valueOf(expiryHours));
    }

    private String buildResetPasswordHtml(String emailLink, long expiryMinutes) {
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <meta name="color-scheme" content="light only">
                  <meta name="supported-color-schemes" content="light">
                  <title>Reset your Craftmeet password</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f5f7f9;-webkit-text-size-adjust:100%;">
                  <div style="display:none;max-height:0;overflow:hidden;opacity:0;font-size:1px;line-height:1px;color:#f5f7f9;">
                    Reset your Craftmeet password by clicking the link below.&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;
                  </div>
                  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f5f7f9;">
                    <tr>
                      <td align="center" style="padding:40px 20px;">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:100%;max-width:600px;">
                          <tr>
                            <td align="center" style="padding:0 0 24px 0;">
                              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                  <td style="vertical-align:middle;padding-right:10px;">{logo_svg}</td>
                                  <td style="vertical-align:middle;font-family:{font_stack};font-size:22px;font-weight:700;letter-spacing:-0.02em;color:#005153;">Craftmeet</td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr>
                            <td style="background-color:#ffffff;border:1px solid #d8dbdf;border-radius:16px;padding:44px 40px;">
                              <h1 style="margin:0 0 10px 0;font-family:{font_stack};font-size:22px;line-height:1.3;font-weight:600;letter-spacing:-0.02em;color:#0f1417;">Reset your password</h1>
                              <p style="margin:0 0 28px 0;font-family:{font_stack};font-size:15px;line-height:1.6;color:#6d7277;">We received a request to reset the password for your Craftmeet account. Click the button below to choose a new password.</p>
                              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                  <td align="center" style="border-radius:10px;background-color:#005153;">
                                    <a href="{email_link}" target="_blank" style="display:inline-block;padding:13px 30px;font-family:{font_stack};font-size:15px;font-weight:600;line-height:1;color:#ffffff;text-decoration:none;border-radius:10px;">Reset my password</a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:28px 0 0 0;font-family:{font_stack};font-size:13px;line-height:1.6;color:#6d7277;">This link expires in <strong style="color:#0f1417;">{expiry_minutes} minutes</strong>. If the button doesn't work, copy and paste this URL into your browser:</p>
                              <p style="margin:8px 0 0 0;font-family:{font_stack};font-size:13px;line-height:1.6;word-break:break-all;">
                                <a href="{email_link}" target="_blank" style="color:#005153;text-decoration:underline;">{email_link}</a>
                              </p>
                              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0">
                                <tr><td style="padding:28px 0 0 0;border-top:1px solid #d8dbdf;"></td></tr>
                              </table>
                              <p style="margin:24px 0 0 0;font-family:{font_stack};font-size:12px;line-height:1.6;color:#6d7277;">If you didn't request a password reset, you can safely ignore this email. Your password will not change.</p>
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding:24px 0 0 0;font-family:{font_stack};font-size:12px;line-height:1.6;color:#6d7277;">
                              &copy; Craftmeet &middot; Real-time structured meetings
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """;
        return html
                .replace("{font_stack}", FONT_STACK)
                .replace("{logo_svg}", LOGO_SVG)
                .replace("{email_link}", emailLink)
                .replace("{expiry_minutes}", String.valueOf(expiryMinutes));
    }

    /**
     * Determines whether the application is running under the development profile.
     *
     * @return {@code true} when {@code dev} is among the active Spring profiles
     */
    private boolean isDev() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

}
