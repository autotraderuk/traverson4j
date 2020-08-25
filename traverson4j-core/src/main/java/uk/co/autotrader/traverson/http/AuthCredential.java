package uk.co.autotrader.traverson.http;

public class AuthCredential {

    private final String username;
    private final String password;
    private final String hostname;
    private final boolean preemptiveAuthentication;

    public AuthCredential(String username, String password, String hostname, boolean preemptiveAuthentication) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.preemptiveAuthentication = preemptiveAuthentication;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public boolean isPreemptiveAuthentication() {
        return preemptiveAuthentication;
    }
}
