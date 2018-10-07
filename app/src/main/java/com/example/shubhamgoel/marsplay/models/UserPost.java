package com.example.shubhamgoel.marsplay.models;

/**
 * Created by shubhamgoel on 05/05/18.
 */

public class UserPost {
    private String id;
    private String postDate;
    private String image1;
    private String image2;
    private String image3;
    private String postText;
    private String created;
    private String modified;
    private String tagged;

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getTagged() {
        return tagged;
    }

    public void setTagged(String tagged) {
        this.tagged = tagged;
    }

}
