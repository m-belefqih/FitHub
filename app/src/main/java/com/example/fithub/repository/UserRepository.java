package com.example.fithub.repository;

import android.net.Uri;

import com.example.fithub.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseStorage storage;

    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Callback pour les opérations d'authentification (inscription, connexion)
    public interface AuthCallback {
        void onSuccess();
        void onFailure(String message);
    }

    // Callback pour le téléchargement d'images
    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String message);
    }

    // Callback pour récupérer le profil utilisateur après l'inscription
    public interface UserProfileCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    // Keep original for backward compatibility, delegate to new method
    public void register(User user, String password, AuthCallback callback) {
        register(user, password, null, callback);
    }

    // New: register and optionally upload profile image (localUri can be null)
    public void register(User user, String password, Uri localUri, AuthCallback callback) {
        if (user == null || user.getEmail() == null || password == null) {
            callback.onFailure("Invalid user or password");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser == null) {
                            callback.onFailure("Could not get created user");
                            return;
                        }

                        String uid = firebaseUser.getUid();
                        user.setUid(uid);

                        if (user.getJoinTime() == null) {
                            user.setJoinTime(new Date());
                        }

                        // If there is an image URI, upload it first
                        if (localUri != null) {
                            uploadImage(localUri, uid, new ImageUploadCallback() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    user.setImage(imageUrl);
                                    saveUserToFirestore(user, callback);
                                }

                                @Override
                                public void onFailure(String message) {
                                    // You can choose to fail registration or save with empty image.
                                    // Here we fail to notify the UI that image upload failed.
                                    callback.onFailure("Image upload failed: " + message);
                                }
                            });
                        } else {
                            // No image -> save user document immediately
                            saveUserToFirestore(user, new AuthCallback() { // ⬅️ On change le callback pour gérer l'envoi d'email
                                @Override
                                public void onSuccess() {
                                    // Envoyer l'e-mail de vérification
                                    FirebaseUser fUser = firebaseAuth.getCurrentUser();
                                    if (fUser != null) {
                                        fUser.sendEmailVerification()
                                                .addOnCompleteListener(task -> {
                                                    // On considère l'inscription comme réussie, même si l'e-mail échoue,
                                                    // car l'utilisateur peut réessayer plus tard.
                                                    callback.onSuccess();
                                                });
                                    } else {
                                        callback.onSuccess(); // User is created, proceed.
                                    }
                                }
                                @Override
                                public void onFailure(String message) {
                                    callback.onFailure(message);
                                }
                            });
                        }
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        callback.onFailure(msg);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage() != null ? e.getMessage() : "Registration failed"));
    }

    private void saveUserToFirestore(User user, AuthCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", user.getUid());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("birthDate", user.getBirthDate());
        data.put("gender", user.getGender());
        data.put("joinTime", user.getJoinTime());
        data.put("image", user.getImage());
        data.put("description", user.getDescription());
        data.put("weight", user.getWeight());
        data.put("height", user.getHeight());
        data.put("score", user.getScore());

        firebaseFirestore.collection("users").document(user.getUid())
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage() != null ? e.getMessage() : "Failed to save user"));
    }

    /**
     * Télécharge une image de profil sur Firebase Storage.
     * @param localUri URI locale de l'image à télécharger.
     * @param uid UID de l'utilisateur pour nommer le fichier.
     * @param callback Callback pour renvoyer l'URL de téléchargement ou l'erreur.
     */
    public void uploadImage(Uri localUri, String uid, ImageUploadCallback callback) {
        if (localUri == null || uid == null) {
            callback.onFailure("Invalid image or user ID");
            return;
        }

        StorageReference storageRef = storage.getReference()
                .child("users")
                .child("profile_pictures")
                .child(uid + ".jpg");

        storageRef.putFile(localUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        callback.onSuccess(uri.toString());
                    }).addOnFailureListener(e -> {
                        callback.onFailure("Failed to get download URL: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Image upload failed: " + e.getMessage());
                });
    }

    /**
     * Connexion d'un utilisateur avec email et mot de passe via Firebase Auth.
     * @param email
     * @param password
     * @param callback
     */
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        callback.onFailure(msg);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage() != null ? e.getMessage() : "Login failed"));
    }

    /**
     * Vérifie si l'utilisateur est déjà connecté via Firebase Auth.
     * @return true si FirebaseUser est non-null (session active), false sinon.
     */
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Récupère le profil utilisateur depuis Firestore.
     * @param callback Callback pour renvoyer l'objet User ou une erreur.
     */
    public void getUserProfile(UserProfileCallback callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        String uid = firebaseUser.getUid();

        firebaseFirestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Firestore peut automatiquement mapper le document à votre classe User
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Failed to parse user data.");
                        }
                    } else {
                        callback.onFailure("User document not found in Firestore.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage() != null ? e.getMessage() : "Failed to fetch user profile.");
                });
    }

    /**
     * Envoie un e-mail de vérification à l'utilisateur Firebase actuellement connecté.
     * @param callback Indique le succès ou l'échec de l'envoi.
     */
    public void sendVerificationEmail(AuthCallback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Failed to send verification email.";
                            callback.onFailure(msg);
                        }
                    });
        } else {
            callback.onFailure("No user is currently logged in.");
        }
    }

    /**
     * Met à jour uniquement les données texte de l'utilisateur dans Firestore.
     */
    public void updateProfileData(User user, AuthCallback callback) {
        if (user == null || user.getUid() == null) {
            callback.onFailure("Utilisateur invalide");
            return;
        }
        // On réutilise votre méthode existante qui enregistre la Map dans Firestore
        saveUserToFirestore(user, callback);
    }

    /**
     * Déconnecte l'utilisateur actuel de Firebase Auth.
     */
    public void logout() {
        // La méthode signOut() met fin à la session utilisateur sur l'appareil.
        firebaseAuth.signOut();
    }
}

