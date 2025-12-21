package com.example.fithub.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fithub.repository.CreatePostRepository;

public class CreatePostViewModel extends ViewModel {

    private final CreatePostRepository repository = new CreatePostRepository();

    private final MutableLiveData<Boolean> postSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Boolean> getPostSuccess() {
        return postSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void createPost(String content) {

        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("Post content cannot be empty");
            return;
        }

        repository.createPost(
                content,
                unused -> postSuccess.setValue(true),
                e -> errorMessage.setValue(e.getMessage())
        );
    }
}

