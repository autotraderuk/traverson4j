package uk.co.autotrader.traverson.datatype;

public class AuthCredential {

    private String username;
    private String password;
    private String hostname;

    public AuthCredential(String username, String password, String hostname) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
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

}
