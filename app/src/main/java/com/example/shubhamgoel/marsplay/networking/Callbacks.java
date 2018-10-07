package com.example.shubhamgoel.marsplay.networking;


import okhttp3.Response;


/**
 * interface for callbacks when Get or Post request are created
 */
public interface Callbacks {

    public void postexecute(String url, Response response);

    public void preexecute(String url);

    public void processResponse(Response response, String url);

    public String preparePostData(String url, String postbody);

}