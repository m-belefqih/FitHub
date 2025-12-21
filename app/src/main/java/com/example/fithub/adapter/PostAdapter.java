package com.example.fithub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fithub.R;
import com.example.fithub.model.Post;
import com.example.fithub.viewmodel.FeedViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private final FeedViewModel viewModel;
    private final Context context;

    private final String currentUserId =
            FirebaseAuth.getInstance().getCurrentUser().getUid();


    public PostAdapter(Context context, FeedViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = posts.get(position);

        // --- DATA ---
        holder.tvContent.setText(post.getContent());
        holder.tvDate.setText(post.getCreatedAt());
        holder.tvComments.setText(String.valueOf(post.getCommentsCount()));
        holder.tvLikes.setText(String.valueOf(post.getLikesCount()));

        // =========================
        // ❤️ LIKE LOGIC (ICI EXACTEMENT)
        // =========================
        boolean isLiked = post.getLikedBy() != null
                && post.getLikedBy().contains(currentUserId);

        if (isLiked) {
            holder.btnLike.setImageResource(R.drawable.ic_like_filled);
            holder.btnLike.setColorFilter(
                    ContextCompat.getColor(context, R.color.primary)
            );
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_like_outline);
            holder.btnLike.setColorFilter(
                    ContextCompat.getColor(context, R.color.white)
            );
        }

        holder.btnLike.setOnClickListener(v -> {
            viewModel.toggleLike(post.getPostId(), currentUserId);
        });

        // =========================
        // 🖼️ IMAGE POST
        // =========================
        if ("image".equals(post.getMediaType()) && post.getMediaUrl() != null && !post.getMediaUrl().isEmpty()) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(convertDriveUrl(post.getMediaUrl()))
                    .into(holder.imgPost);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }

        // =========================
        // 👤 PROFILE IMAGE + USERNAME
        // =========================
        holder.tvUsername.setText(post.getUsername());

        if (post.getUserImage() != null && !post.getUserImage().isEmpty()) {
            Glide.with(context)
                    .load(convertDriveUrl(post.getUserImage()))
                    .circleCrop()
                    .placeholder(R.drawable.userprofile)
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.coach);
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfile;
        ShapeableImageView imgPost;
        TextView tvUsername, tvDate, tvContent, tvLikes, tvComments;
        ImageButton btnLike;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.img_profile);
            imgPost = itemView.findViewById(R.id.img_post);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            tvComments = itemView.findViewById(R.id.tv_comments);
            btnLike = itemView.findViewById(R.id.btn_like);
        }
    }

    // Google Drive → Direct image
    private String convertDriveUrl(String url) {
        return url.replace("file/d/", "uc?export=view&id=")
                .replace("/view?usp=sharing", "");
    }
}

