package javanesecoffee.com.blink.entities;
import org.json.JSONException;
import org.json.JSONObject;
import javanesecoffee.com.blink.api.BLinkApiException;

public class User {

    private String username;
    private String displayname;
    private String bio;
    private String email;
    private String position;
    private String company;
    private String linkedin;
    private String facebook;
    private String instagram;
    private Connection connection;

    public User(String username) {
        this.username = username;
        displayname = "";
        bio = "";
        email = "";
        position = "";
        company = "";
        linkedin = "";
        facebook = "";
        instagram = "";
    }
    /**
     *
     * @param data input response.data json object for user
     */
    public User(JSONObject data) throws BLinkApiException {
        try {
            this.username = data.getString("username");
            this.displayname = data.getString("displayname");
            this.bio = data.getString("bio");
            this.email = data.getString("email");
            this.position = data.getString("position");
            this.company = data.getString("company");
            this.linkedin = data.getString("linkedin");
            this.facebook = data.getString("facebook");
            this.instagram = data.getString("instagram");

            try {
                JSONObject connectionObject = data.getJSONObject("connection");
                this.connection = new Connection(connectionObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (BLinkApiException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public String getDisplayname() { return displayname; }
    public String getCompany() { return company; }
    public String getBio() { return bio; }
    public String getPosition() { return position; }
    public String getLinkedin() { return linkedin; }
    public String getFacebook() { return facebook; }
    public String getInstagram() { return instagram; }
    public Connection getConnection() {
        return connection;
    }
}
