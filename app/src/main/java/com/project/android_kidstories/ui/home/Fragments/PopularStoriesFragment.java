package com.project.android_kidstories.ui.home.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixplicity.easyprefs.library.Prefs;
import com.project.android_kidstories.Api.Api;
import com.project.android_kidstories.Api.Responses.bookmark.BookmarkResponse;
import com.project.android_kidstories.Api.Responses.bookmark.UserBookmarkResponse;
import com.project.android_kidstories.Api.Responses.story.StoryAllResponse;
import com.project.android_kidstories.Api.RetrofitClient;
import com.project.android_kidstories.Model.Story;
import com.project.android_kidstories.R;
import com.project.android_kidstories.adapters.BookmarksAdapter;
import com.project.android_kidstories.adapters.RecyclerStoriesAdapter;
import com.project.android_kidstories.ui.profile.BookmarksFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularStoriesFragment extends Fragment implements RecyclerStoriesAdapter.OnBookmarked {
    private RecyclerStoriesAdapter adapter;
    private ProgressBar popular_bar;
    RecyclerView recyclerView;
    private Api service;
    private boolean isAddSuccessful;
    int initBookmarkId;

    public static PopularStoriesFragment newInstance(){return new PopularStoriesFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_popularstories,container,false);
        ButterKnife.bind(this,v);

        popular_bar = v.findViewById(R.id.popular_stories_bar);

        popular_bar.setVisibility(View.VISIBLE);

        /*Create handle for the RetrofitInstance interface*/
        service = RetrofitClient.getInstance().create(Api.class);
        Call<StoryAllResponse> stories = service.getAllStories();

        stories.enqueue(new Callback<StoryAllResponse>() {
            @Override
            public void onResponse(Call<StoryAllResponse> call, Response<StoryAllResponse> response) {
                //  generateCategoryList(response.body(),v);
                popular_bar.setVisibility(View.GONE);

                recyclerView = v.findViewById(R.id.recyclerView);
                if (response.isSuccessful()) {
                    adapter = new RecyclerStoriesAdapter(getContext(), sortList(response.body()),PopularStoriesFragment.this);
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }else{
                    Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StoryAllResponse> call, Throwable t) {
                popular_bar.setVisibility(View.INVISIBLE);

                Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private StoryAllResponse sortList(StoryAllResponse allResponse){
        List<Story> allStories = allResponse.getData();


        StoryComparitor storyComparitor = new StoryComparitor();
        Collections.sort(allStories,storyComparitor);

        StoryAllResponse response = new StoryAllResponse();
        response.setData(allStories);
        return response;

    }

    @Override
    public boolean onBookmarkAdded(int storyId) {
        String token = "Bearer "+ Prefs.getString("Token","");
        Call<BookmarkResponse> addBookmark= service.bookmarkStory(token,storyId);
        addBookmark.enqueue(new Callback<BookmarkResponse>() {
            @Override
            public void onResponse(Call<BookmarkResponse> call, Response<BookmarkResponse> response) {
                if (response.isSuccessful()) {
                    isAddSuccessful = response.body().getData();
                }else {
                    isAddSuccessful = false;
                }
            }

            @Override
            public void onFailure(Call<BookmarkResponse> call, Throwable t) {
                isAddSuccessful = false;
            }
        });
        Log.e("ISADDSUCCESSFUL",isAddSuccessful+"");
        return isAddSuccessful;
    }

    @Override
    public int isAlreadyBookmarked(int storyId, int pos) {
        String token = "Bearer "+ Prefs.getString("Token","");

        Call<UserBookmarkResponse> bookmarks = service.getUserBookmarks(token);

        bookmarks.enqueue(new Callback<UserBookmarkResponse>() {
            @Override
            public void onResponse(Call<UserBookmarkResponse> call, Response<UserBookmarkResponse> response) {
                if (response.isSuccessful()) {
                    List<Story> data = response.body().getData();
                    for(Story s: data){
                        if(s.getId() == storyId){
                           initBookmarkId = s.getId();
                        }
                    }
                }else {
                    Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserBookmarkResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("INITBOOKMARK",initBookmarkId+"");
        return initBookmarkId;
    }

    public class StoryComparitor implements Comparator<Story> {

        @Override
        public int compare(Story story1, Story story2) {
            int likes_dislikes_1 = story1.getDislikesCount()+story1.getLikesCount();
            int likes_dislikes_2 = story2.getDislikesCount()+story2.getLikesCount();

            if(likes_dislikes_1 > likes_dislikes_2)
                return -1;
            else if(likes_dislikes_1 > likes_dislikes_2)
                return +1;
            else
                return 0;
        }
    }

}
