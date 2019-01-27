package fr.wildcodeschool.airbusproject.Model;

import android.net.Uri;

public class User {

    private String id;
    private String username;
    private Uri imageURL;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Uri getImageURL() {
        return imageURL;
    }

    public void setImageURL(Uri imageURL) {
        this.imageURL = imageURL;
    }
}
