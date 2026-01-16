package com.example.fithub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fithub.R;
import com.example.fithub.model.Messages;
import com.example.fithub.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ChatFragment extends Fragment {

    private LinearLayout chatContainer;
    private ScrollView chatScroll;          // ← Added this
    private TextInputEditText etMessage;
    private View btnSend;
    private TextView tvChatUser;
    private ImageView imgChatUser;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference messagesRef, mirroredRef, usersRef;
    private String currentUserId;
    private String otherUserId = "COACH_UID_HERE"; // ← Replace with real coach UID

    public ChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        // Your views
        chatContainer = view.findViewById(R.id.chatContainer);
        chatScroll = view.findViewById(R.id.chatScroll);         // ← Find the ScrollView
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        tvChatUser = view.findViewById(R.id.tvChatUser);
        imgChatUser = view.findViewById(R.id.imgChatUser);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        // TODO: Replace with actual coach UID (or pass via arguments later)
        otherUserId = "replace_with_coach_uid";

        // Firebase references
        messagesRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(currentUserId).child(otherUserId);

        mirroredRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(otherUserId).child(currentUserId);

        usersRef = FirebaseDatabase.getInstance().getReference("Users").child(otherUserId);

        // Load coach profile
        usersRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    tvChatUser.setText(user.getUsername() != null ? user.getUsername() : "Coach");
                    String image = user.getImage();
                    if (image != null && !image.isEmpty()) {
                        Picasso.get().load(image).placeholder(R.drawable.userprofile).into(imgChatUser);
                    }
                }
            }
        });

        // Send message
        btnSend.setOnClickListener(v -> sendMessage());

        // Load existing + new messages
        loadMessages();

        return view;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String time = String.valueOf(System.currentTimeMillis());

        Messages msg = new Messages(currentUserId, otherUserId, text, time);

        String key = messagesRef.push().getKey();

        // Mirror to both users
        messagesRef.child(key).setValue(msg);
        mirroredRef.child(key).setValue(msg);

        etMessage.setText("");
    }

    private void loadMessages() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Messages msg = snapshot.getValue(Messages.class);
                if (msg != null) {
                    addMessageToView(msg);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addMessageToView(Messages msg) {
        // Inflate correct bubble layout
        int layoutRes = msg.getSender().equals(currentUserId)
                ? R.layout.item_outgoing_message
                : R.layout.item_incoming_message;

        View messageView = LayoutInflater.from(getContext()).inflate(layoutRes, chatContainer, false);

        TextView textView = messageView.findViewById(R.id.message_text);
        textView.setText(msg.getMessage());

        chatContainer.addView(messageView);

        // Proper auto-scroll to bottom
        chatScroll.post(() -> chatScroll.fullScroll(View.FOCUS_DOWN));
    }
}