package me.kaotich00.fwauctionhouse.objects;

public class PendingToken {

    private int sessionId;

    private String username;
    private String token;

    public PendingToken(int sessionId, String username, String token) {
        this.sessionId = sessionId;
        this.username = username;
        this.token = token;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
