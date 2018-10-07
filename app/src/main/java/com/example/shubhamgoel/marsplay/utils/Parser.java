package com.example.shubhamgoel.marsplay.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;


import com.example.shubhamgoel.marsplay.models.UserPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shubhamgoel on 06/05/18.
 */

public class Parser {


    public static ArrayList<UserPost> parsePosts(String response, Context context) {
        ArrayList<UserPost> userPosts = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.optJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectPost = jsonArray.optJSONObject(i);
                UserPost userPost = new UserPost();
                userPost.setId(objectPost.optString("id"));
                userPost.setPostDate(objectPost.optString("created"));
                userPost.setImage1(objectPost.optString("photo1"));
                userPost.setImage2(objectPost.optString("photo2"));
                userPost.setImage3(objectPost.optString("photo3"));
                userPost.setPostText(objectPost.optString("title"));
                userPost.setCreated(objectPost.optString("created"));
                userPost.setModified(objectPost.optString("modified"));
                userPost.setTagged(objectPost.optString("tags"));

                JSONObject userObj = objectPost.getJSONObject("user");
                userPosts.add(userPost);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userPosts;
    }

    public static UserPost parseSinglePost(String response, Context context) {
        UserPost userPost = new UserPost();

        try {
            JSONObject objectPost = new JSONObject(response);
            userPost.setId(objectPost.optString("id"));
            userPost.setPostDate(objectPost.optString("created"));
            userPost.setImage1(objectPost.optString("photo1"));
            userPost.setImage2(objectPost.optString("photo2"));
            userPost.setImage3(objectPost.optString("photo3"));
            userPost.setPostText(objectPost.optString("title"));
            userPost.setCreated(objectPost.optString("created"));
            userPost.setModified(objectPost.optString("modified"));
            userPost.setTagged(objectPost.optString("tags"));

            JSONObject userObj = objectPost.getJSONObject("user");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userPost;
    }


}
