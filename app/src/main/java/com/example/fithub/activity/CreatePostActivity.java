package com.example.fithub.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fithub.databinding.ActivityCreatePostBinding;
import com.example.fithub.viewmodel.CreatePostViewModel;

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding binding;
    private CreatePostViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this)
                .get(CreatePostViewModel.class);

        // ❌ Cancel
        binding.btnClose.setOnClickListener(v -> finish());

        // ✅ Post
        binding.btnPost.setOnClickListener(v -> {
            String content = binding.etContent.getText().toString();
            viewModel.createPost(content);
        });

        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getPostSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Post published successfully", Toast.LENGTH_SHORT).show();
                finish(); // retour au Feed
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

