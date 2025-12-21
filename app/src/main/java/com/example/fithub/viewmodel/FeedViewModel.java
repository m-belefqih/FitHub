package com.example.fithub.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fithub.model.Post;
import com.example.fithub.repository.FeedRepository;

import java.util.List;

public class FeedViewModel extends ViewModel {

    private final FeedRepository repository = new FeedRepository();
    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>();

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public void loadPosts() {
        repository.getPosts(posts);
    }

    public void likePost(String postId) {
        repository.likePost(postId);
    }

    public void toggleLike(String postId, String userId) {
        repository.toggleLike(postId, userId);
    }
}







