package com.example.shubhamgoel.marsplay;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.example.shubhamgoel.marsplay.adapter.UserPostListAdapter;
import com.example.shubhamgoel.marsplay.models.UserPost;
import com.example.shubhamgoel.marsplay.networking.Callbacks;
import com.example.shubhamgoel.marsplay.networking.GetRequest;
import com.example.shubhamgoel.marsplay.utils.Constants;
import com.example.shubhamgoel.marsplay.utils.Parser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostListFragment extends Fragment implements Callbacks {


    String url_user_post = Constants.base_url + "api/v1/user-post/?ordering=-created&posttype=photo&show_self=true";
    private RecyclerView rv;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private UserPostListAdapter userPostListAdapter;
    private ArrayList<UserPost> userPostTemp = new ArrayList<>();
    private ProgressBar progressBar;

    public PostListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_post_list, container, false);
        rv = v.findViewById(R.id.recyclerView);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        } else {
            rv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }

        String tag = "";
        progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        url_user_post += tag;
        Log.e("url", url_user_post);
        new GetRequest().getRequest(url_user_post, PostListFragment.this, getActivity());
        userPostListAdapter = new UserPostListAdapter(userPosts, getActivity());
        rv.setAdapter(userPostListAdapter);
        return v;
    }

    @Override
    public void postexecute(String url, Response response) {
        progressBar.setVisibility(View.GONE);
        if (url.equalsIgnoreCase(url_user_post)) {
            userPostListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void preexecute(String url) {

    }

    @Override
    public void processResponse(Response response, String url) {
        try {
            String body = null;
            body = response.body().string();
            Log.e("response ", body);

            if (url.equalsIgnoreCase(url_user_post)) {


                if (response.code() == 400) {
                    return;
                }
                Log.e("TAG", body);
                userPostTemp = Parser.parsePosts(body, getActivity());
                userPosts.addAll(userPostTemp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String preparePostData(String url, String postbody) {
        return null;
    }
}
