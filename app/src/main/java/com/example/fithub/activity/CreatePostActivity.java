package com.example.fithub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fithub.R;
import com.example.fithub.model.Post;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    // 1. Déclaration des vues et des outils Firebase
    private EditText postContentEditText;
    private ImageView profileImage, imagePreview;
    private TextView userNameTextView;
    private FloatingActionButton addImageButton, submitPostButton;
    private MaterialToolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Uri imageUri; // Stocke l'URI de l'image sélectionnée par l'utilisateur

    // 2. Lanceur moderne pour récupérer le résultat de la galerie d'images
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    imagePreview.setImageURI(imageUri);
                    imagePreview.setVisibility(View.VISIBLE); // Affiche l'aperçu de l'image
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 3. Charger le layout que vous avez créé
        setContentView(R.layout.activity_create_post);

        // Initialisation des services Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // 4. Liaison des vues du layout XML avec le code Java
        toolbar = findViewById(R.id.toolbar_create_post);
        profileImage = findViewById(R.id.createpost_profile_image);
        userNameTextView = findViewById(R.id.createpost_username);
        postContentEditText = findViewById(R.id.createpost_description_text);
        imagePreview = findViewById(R.id.image_preview_container);
        addImageButton = findViewById(R.id.createpost_add_image_button);
        submitPostButton = findViewById(R.id.createpost_post_complete_button);

        // 5. Configuration des actions des boutons
        toolbar.setNavigationOnClickListener(v -> finish()); // Bouton "retour"
        addImageButton.setOnClickListener(v -> openImageChooser());
        submitPostButton.setOnClickListener(v -> submitPost());

        // 6. Charger les informations de l'utilisateur qui est connecté
        loadUserInfo();
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // C'EST ICI QUE LE NOM S'AFFICHE DYNAMIQUEMENT
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                userNameTextView.setText(displayName);
            } else {
                // Solution de secours si l'utilisateur n'a pas de nom d'affichage
                userNameTextView.setText(currentUser.getEmail());
            }

            // Charger la photo de profil de l'utilisateur (si elle existe)
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile) // Icône par défaut si pas de photo
                    .circleCrop()
                    .into(profileImage);
        }
    }

    // Méthode pour ouvrir la galerie
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // Méthode principale pour envoyer le post
    private void submitPost() {
        String contentText = postContentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(contentText) && imageUri == null) {
            Toast.makeText(this, "You must write something or add an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true); // Bloque les boutons pour éviter les doubles clics

        if (imageUri != null) {
            uploadImageAndCreatePost(contentText); // S'il y a une image, on l'upload d'abord
        } else {
            createPostInFirestore(contentText, null); // Sinon, on crée le post directement
        }
    }

    // Étape A : Uploader l'image vers Firebase Storage
    private void uploadImageAndCreatePost(String contentText) {
        StorageReference storageRef = storage.getReference().child("post_images/" + UUID.randomUUID().toString());

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            createPostInFirestore(contentText, imageUrl); // Étape B
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    setLoadingState(false);
                });
    }

    // Étape B : Créer le document du post dans Firestore
    private void createPostInFirestore(String contentText, String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            setLoadingState(false);
            return;
        }

        // ================== CORRECTION APPLIQUÉE ICI ==================
        // Le constructeur est appelé avec 5 arguments, ce qui est correct
        // si vous avez adapté le constructeur de votre classe Post.
        // Si votre classe Post attend les compteurs, vous devez les ajouter.
        // Puisque nous ne savons pas quelle solution vous avez choisie,
        // je vous donne un constructeur qui initialise TOUT.
        // Si vous avez une erreur ici, c'est que votre classe Post a
        // un constructeur différent.
        Post newPost = new Post(
                currentUser.getUid(),
                currentUser.getDisplayName(),
                currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null,contentText,
                imageUrl,
                // On active ces lignes pour fournir les valeurs initiales
                0, // likeCount initial
                0  // commentCount initial
        );

        // ===============================================================

        db.collection("posts").add(newPost)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Post published!", Toast.LENGTH_SHORT).show();
                    finish(); // Ferme l'activité et retourne au fil d'actualité
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error publishing post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    setLoadingState(false);
                });
    }

    // Petite méthode pour activer/désactiver les boutons pendant le chargement
    private void setLoadingState(boolean isLoading) {
        submitPostButton.setEnabled(!isLoading);
        addImageButton.setEnabled(!isLoading);
    }
}
