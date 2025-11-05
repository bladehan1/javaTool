package third.elastic;


public class ElasticsearchProperties {

    public static final int ADDRESS_LENGTH = 2;

    private String scheme = "http";
    private String[] address;
    private String username;
    private String password;

    public ElasticsearchProperties() {
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
