package com.example.fithub.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fithub.activity.CreatePostActivity;
import com.example.fithub.adapter.PostAdapter;
import com.example.fithub.databinding.FragmentFeedBinding;
import com.example.fithub.viewmodel.FeedViewModel;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private FeedViewModel viewModel;
    private PostAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        adapter = new PostAdapter(requireContext(), viewModel);
        binding.feedRecyclerView.setAdapter(adapter);

        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            adapter.setPosts(posts);
        });

        viewModel.loadPosts();

        binding.fabNewPost.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreatePostActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}