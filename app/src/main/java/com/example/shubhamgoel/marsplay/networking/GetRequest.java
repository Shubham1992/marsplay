package com.example.shubhamgoel.marsplay.networking;

import android.content.Context;
import android.os.AsyncTask;


import com.example.shubhamgoel.marsplay.utils.SharedPrefUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by shubhamgoel on 08/05/18.
 */

public class GetRequest {
    String _url;
    Context _context;
    private Callbacks _callback;

    public void getRequest(final String url, final Callbacks callback, final Context context) {

        _url = url;
        _context = context;
        _callback = callback;

        new GetRequestAsync().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    class GetRequestAsync extends AsyncTask<String, String, Response> {

        private Response _response;

        @Override
        protected Response doInBackground(String... arg) {
            OkHttpClient client = new OkHttpClient();
            Request request = null;
            if (!SharedPrefUtil.getSharedPref(_context).getString("key", "2167c9c349a6b6f1afe759b3f73ef4cef4cc07ea").equalsIgnoreCase("")) {
                request = new Request.Builder()
                        .header("Authorization", "Token " + SharedPrefUtil.getSharedPref(_context).getString("key", "2167c9c349a6b6f1afe759b3f73ef4cef4cc07ea"))
                        .url(_url)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(_url)
                        .build();
            }


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
