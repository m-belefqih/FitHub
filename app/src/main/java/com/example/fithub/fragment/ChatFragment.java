package com.example.fithub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithub.R;
import com.example.fithub.model.Messages;
import com.example.fithub.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private RecyclerView chatList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messagesRef, usersRef;
    private String currentUserId;

    private FirebaseRecyclerAdapter<Messages, ChatViewHolder> adapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Find RecyclerView (make sure you have this ID in fragment_chat.xml)
        chatList = view.findViewById(R.id.chat_list);
        chatList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);      // Newest chats at top
        layoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(layoutManager);

        // Firebase setup
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        messagesRef = FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child(currentUserId);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupAdapter();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void setupAdapter() {
        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(messagesRef, Messages.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Messages, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Messages model) {
                // Each child node key is the other user's UID
                String otherUserId = getRef(position).getKey();

                // Fetch the other user's info
                usersRef.child(otherUserId).get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            holder.userName.setText(user.getUsername() != null ? user.getUsername() : "Unknown");

                            String image = user.getImage();
                            if (image != null && !image.isEmpty()) {
                                Picasso.get()
                                        .load(image)
                                        .placeholder(R.drawable.userprofile)
                                        .into(holder.userImage);
                            } else {
                                holder.userImage.setImageResource(R.drawable.userprofile);
                            }
                        }
                    }
                });

                // Click to open private chat (replace with your actual navigation)
                holder.itemView.setOnClickListener(v -> {
                    // Example: If you have a ChatDetailActivity
                    // Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                    // intent.putExtra("otherUserId", otherUserId);
                    // startActivity(intent);

                    // Or if using Navigation Component + Fragment:
                    // Bundle bundle = new Bundle();
                    // bundle.putString("otherUserId", otherUserId);
                    // NavHostFragment.findNavController(ChatFragment.this)
                    //     .navigate(R.id.action_chatFragment_to_chatDetailFragment, bundle);
                });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_user, parent, false);
                return new ChatViewHolder(view);
            }
        };

        chatList.setAdapter(adapter);
    }

    // ViewHolder for each chat list item
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView userName;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
        }
    }
}