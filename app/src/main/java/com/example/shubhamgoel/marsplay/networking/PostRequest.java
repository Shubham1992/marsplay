package com.example.shubhamgoel.marsplay.networking;

import android.content.Context;
import android.os.AsyncTask;


import com.example.shubhamgoel.marsplay.utils.SharedPrefUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shubhamgoel on 07/05/18.
 */

public class PostRequest {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_DATA = MediaType.parse("application/form-data; charset=utf-8");

    private String _url;
    private Context _context;
    private Callbacks _callback;
    private RequestBody _postBody;
    private Request request;
    private RequestBody _postBody_multipart;

    public void postRequest(final String postUrl, String postBody, final Callbacks callback, final Context context) {
        RequestBody body = RequestBody.create(JSON, postBody);
        _postBody = body;
        _url = postUrl;
        _context = context;
        _callback = callback;

        new PostRequestAsync().execute();
    }

    public void postRequest(final String postUrl, RequestBody postBody,
                            final Callbacks callback, final Context context, final String reqType) {

        _postBody = postBody;
        _url = postUrl;
        _context = context;
        _callback = callback;

        new PostRequestAsync().execute();
    }

    public void postRequest(final String postUrl, RequestBody postBody, final Callbacks callback, final Context context) {

        _postBody = postBody;
        _url = postUrl;
        _context = context;
        _callback = callback;

        new PostRequestAsync().execute();
    }


    class PostRequestAsync extends AsyncTask<String, String, Response> {

        @Override
        protected Response doInBackground(String... strings) {
            OkHttpClient client = client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            if (SharedPrefUtil.getSharedPref(_context).getString("key", "2167c9c349a6b6f1afe759b3f73ef4cef4cc07ea").length() > 0)
                request = new Request.Builder()
                        .header("Authorization", "Token " + SharedPrefUtil.getSharedPref(_context).getString("key", "2167c9c349a6b6f1afe759b3f73ef4cef4cc07ea"))
                        .url(_url)
                        .post(_postBody)
                        .build();
            else
                request = new Request.Builder()
                        .url(_url)
                        .post(_postBody)
                        .build();


            try {
                Response response = client.newCall(request).execute();
                _callback.processResponse(response, _url);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            _callback.postexecute(_url, response);
        }
    }


}
