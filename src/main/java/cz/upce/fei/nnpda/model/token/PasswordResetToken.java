package cz.upce.fei.nnpda.model.token;

// Pomocná třída
public class PasswordResetToken {
    private final String code;
    private final String username;
    private final long expiresAt; // [ms]

    public PasswordResetToken(String code, String username, long expiresAt) {
        this.code = code;
        this.username = username;
        this.expiresAt = expiresAt;
    }

    public String getCode() { return code; }
    public String getUsername() { return username; }
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
