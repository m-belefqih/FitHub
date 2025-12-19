package com.example.fithub.ui.feed;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithub.R;
import com.example.fithub.ui.post.CreatePostActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    // UI
    private RecyclerView feedRecyclerView;
    private FloatingActionButton fabNewPost;
    private TextInputEditText searchEditText;

    // Data
    private PostAdapter postAdapter;
    private List<Post> postList;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupFab();

        loadPosts(); // temporaire (mock data)

        return view;
    }

    // 🔧 Init UI
    private void initViews(View view) {
        feedRecyclerView = view.findViewById(R.id.feed_recycler_view);
        fabNewPost = view.findViewById(R.id.fab_new_post);

        searchEditText = view.findViewById(R.id.search_layout)
                .findViewById(com.google.android.material.R.id.textinput_edittext);
    }

    // 📜 RecyclerView
    private void setupRecyclerView() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getContext());

        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedRecyclerView.setAdapter(postAdapter);
        feedRecyclerView.setHasFixedSize(true);
    }

    // 🔍 Search filter
    private void setupSearch() {
        if (searchEditText == null) return;

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ✏️ New Post
    private void setupFab() {
        fabNewPost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });
    }

    // 📦 Fake data (temporaire)
    private void loadPosts() {
        postList.add(new Post(
                "Abdelhadi",
                "1 min ago",
                "First workout of the day 💪",
                null,
                12,
                3
        ));

        postList.add(new Post(
                "Aya",
                "10 min ago",
                "Morning run 🏃‍♀️🔥",
                null,
                20,
                5
        ));

        postAdapter.notifyDataSetChanged();
    }
}
