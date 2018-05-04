package tools.jwt;

import java.util.Date;

/**
 * @author wangchunyang@gmail.com
 */
public class Token {
    private boolean valid;
    private String userId;
    private String wrappedToken;
    private Date expirationTime;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWrappedToken(String wrappedToken) {
        this.wrappedToken = wrappedToken;
    }

    public String getWrappedToken() {
        return wrappedToken;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    @Override
    public String toString() {
        return "Token{" +
                "valid=" + valid +
                ", userId='" + userId + '\'' +
                ", wrappedToken='" + wrappedToken + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
