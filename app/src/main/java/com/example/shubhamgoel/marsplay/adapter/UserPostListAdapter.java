package com.example.shubhamgoel.marsplay.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shubhamgoel.marsplay.R;
import com.example.shubhamgoel.marsplay.models.UserPost;
import com.example.shubhamgoel.marsplay.networking.Callbacks;
import com.example.shubhamgoel.marsplay.utils.Constants;
import com.example.shubhamgoel.marsplay.utils.TouchImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Response;

public class UserPostListAdapter extends RecyclerView.Adapter<UserPostListAdapter.ViewHolder> implements Callbacks {
    private ArrayList<UserPost> mDataset;
    private ArrayList<UserPost> mVisibleDataset;
    private int previous_selection = 0;
    private Context mContext;
    private String url_post_comment = Constants.base_url + "api/v1/comment/";
    private int currentPosition = -1;
    private LinearLayout currentCommentContainer;


    // Provide a suitable constructor (depends on the kind of dataset)
    public UserPostListAdapter(ArrayList<UserPost> myDataset, Context context) {
        mVisibleDataset = myDataset;
        mDataset = myDataset;
        this.mContext = context;
        setHasStableIds(true);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_post_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (mVisibleDataset.get(position).getImage1().startsWith("http")) {
            holder.image.setVisibility(View.VISIBLE);
            {

                holder.image.setVisibility(View.VISIBLE);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                Glide.with(mContext)
                        .load(mVisibleDataset.get(position).getImage1())
                        .into(holder.image);
            }

        } else holder.image.setVisibility(View.GONE);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.zoom_image_popup, null);
                alertDialog.setView(view);
                final TouchImageView touchImageView = view.findViewById(R.id.touchImage);
                Glide.with(mContext)
                        .load(mVisibleDataset.get(position).getImage1()).placeholder(R.drawable.placeholder)
                        .into(touchImageView);

                alertDialog.create().show();
            }
        });


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void postexecute(String url, Response response) {

        if (url.equalsIgnoreCase(url_post_comment)
                && (response.code() == 201 || response.code() == 200)) {

        }
    }

    @Override
    public void preexecute(String url) {

    }

    @Override
    public void processResponse(Response response, String url) {
        String body = null;
        try {
            body = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("comment response", body);
        if (response.code() == 400) {
            return;
        }

    }

    @Override
    public String preparePostData(String url, String postbody) {
        return null;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ImageView image;


        public ViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.image);


        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}