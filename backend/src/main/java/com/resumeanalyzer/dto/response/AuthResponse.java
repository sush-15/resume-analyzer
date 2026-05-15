package com.resumeanalyzer.dto.response;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String fullName;
    private String role;
    private String message;

    public AuthResponse() {}

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getMessage() { return message; }

    public void setToken(String token) { this.token = token; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setMessage(String message) { this.message = message; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token, tokenType = "Bearer", email, fullName, role, message;

        public Builder token(String v) { this.token = v; return this; }
        public Builder tokenType(String v) { this.tokenType = v; return this; }
        public Builder email(String v) { this.email = v; return this; }
        public Builder fullName(String v) { this.fullName = v; return this; }
        public Builder role(String v) { this.role = v; return this; }
        public Builder message(String v) { this.message = v; return this; }

        public AuthResponse build() {
            AuthResponse r = new AuthResponse();
            r.token = token; r.tokenType = tokenType; r.email = email;
            r.fullName = fullName; r.role = role; r.message = message;
            return r;
        }
    }
}
