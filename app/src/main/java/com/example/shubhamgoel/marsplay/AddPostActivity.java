package com.example.shubhamgoel.marsplay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;


import com.example.shubhamgoel.marsplay.networking.Callbacks;
import com.example.shubhamgoel.marsplay.networking.PostRequest;
import com.example.shubhamgoel.marsplay.utils.Constants;
import com.example.shubhamgoel.marsplay.utils.Util;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPostActivity extends AppCompatActivity implements Callbacks {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    private ImageView imageView;
    private String filePathSelected;
    private EditText
            etTitle;
    private String url_create_userpost = Constants.base_url + "api/v1/user-post/";
    private TextView singer, dancer, musician;
    private String tag = "";
    private TextView next;
    private ProgressDialog progressDialog;
    private VideoView videoView;
    private String body = "";
    private ImageView back;
    private TextView other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post2);

        imageView = findViewById(R.id.imageView);
        back = findViewById(R.id.back);
        videoView = findViewById(R.id.videoView);
        etTitle = findViewById(R.id.etTitle);

        filePathSelected = getIntent().getStringExtra("file");
        if (filePathSelected.endsWith(".png") || filePathSelected.endsWith(".jpg")) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePathSelected);
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
        } else {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoPath(filePathSelected);
            //videoView.start();
        }
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTitle.getText().toString().length() == 0) {
                    etTitle.setText(" ");
                }
                progressDialog.show();
                new PostRequest().postRequest(url_create_userpost, prepareRequestBody(), AddPostActivity.this, AddPostActivity.this);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressDialog = new ProgressDialog(AddPostActivity.this);
        progressDialog.setMessage("Uploading Post. This may take some time");
    }


    @Override
    public void postexecute(String url, Response response) {
        progressDialog.dismiss();
        if (response == null) {
            Util.showToast("Your post could not be uploaded. Please check your network and try later", AddPostActivity.this);
            return;

        }
        if (response.isSuccessful()) {
            Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Util.showToast(body, AddPostActivity.this);
        }
    }

    @Override
    public void preexecute(String url) {

    }

    @Override
    public void processResponse(Response response, String url) {
        try {
            body = response.body().string();
            Log.e("create post", body);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String preparePostData(String url, String postbody) {
        return null;
    }

    private RequestBody prepareRequestBody() {
        MultipartBody requestBody;
        if (filePathSelected == null || new File(filePathSelected) == null) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", etTitle.getText().toString())
                    .addFormDataPart("description", "-")
                    .addFormDataPart("photo2", "-")
                    .addFormDataPart("tags", tag)
                    .addFormDataPart("photo3", "-").build();
        } else {
            File file = new File(filePathSelected);
            if (file.getName().endsWith(".mp4"))
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", etTitle.getText().toString())
                        .addFormDataPart("description", "-")
                        .addFormDataPart("photo2", "-")
                        .addFormDataPart("photo3", "-")
                        .addFormDataPart("tags", tag)
                        .addFormDataPart("posttype", "video")
                        .addFormDataPart("photo1", file.getName(),
                                RequestBody.create(MEDIA_TYPE_MP4, file))
                        .build();

            else requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", etTitle.getText().toString())
                    .addFormDataPart("description", "-")
                    .addFormDataPart("photo2", "-")
                    .addFormDataPart("photo3", "-")
                    .addFormDataPart("tags", tag)
                    .addFormDataPart("posttype", "photo")
                    .addFormDataPart("photo1", file.getName(),
                            RequestBody.create(MEDIA_TYPE_PNG, file))
                    .build();
        }
        return requestBody;
    }

}
