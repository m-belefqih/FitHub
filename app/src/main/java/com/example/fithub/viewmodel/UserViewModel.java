package com.example.fithub.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fithub.model.User;
import com.example.fithub.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;

    // --- ÉTATS ---
    // Ces objets LiveData représentent l'état de l'interface utilisateur.
    // L'UI observe ces données pour se mettre à jour automatiquement en cas de changement.

    // Représente l'état du résultat de la tentative de connexion (ex: "success" ou message d'erreur).
    private MutableLiveData<String> loginResult = new MutableLiveData<>();

    // Représente l'état du résultat de la tentative d'inscription (ex: "success" ou message d'erreur).
    private MutableLiveData<String> registerResult = new MutableLiveData<>();

    // Représente l'état initial de la session (déjà connecté ou non).
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    // Représente l'état de l'URL de l'image après upload
    private MutableLiveData<String> imageUrl = new MutableLiveData<>();

    // Représente les données complètes de l'utilisateur connecté.
    private MutableLiveData<User> userProfile = new MutableLiveData<>();

    // Représente l'état de l'erreur de récupération du profil
    private MutableLiveData<String> profileError = new MutableLiveData<>();

    // Nouvel état pour la mise à jour
    private MutableLiveData<String> updateResult = new MutableLiveData<>();

    public UserViewModel() {
        userRepository = new UserRepository();

        // Initialise l'état de connexion au démarrage du ViewModel
        isLoggedIn.setValue(userRepository.isUserLoggedIn());
    }

    // NOUVEAU CALLBACK POUR L'UI (EmailVerificationActivity)
    public interface VerificationCallback {
        void onSuccess();
        void onFailure(String message);
    }

    // --- GETTERS ---
    // Getters publics pour que l'UI (Activity/Fragment) puisse observer les états.
    // On expose une version non-modifiable (LiveData) pour respecter l'encapsulation.
    public LiveData<String> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getRegisterResult() {
        return registerResult;
    }

    // Getter pour que l'UI puisse observer l'état de connexion
    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public LiveData<String> getImageUrl() {
        return imageUrl;
    }

    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public LiveData<String> getProfileError() {
        return profileError;
    }

    public LiveData<String> getUpdateResult() {
        return updateResult;
    }


    // --- ÉVÉNEMENTS ---
    // Ces méthodes publiques sont appelées par l'UI pour déclencher des actions
    // ou des traitements, comme une tentative de connexion ou d'inscription.

    /**
     * Événement déclenché par l'UI pour demander la connexion d'un utilisateur.
     * @param email L'email de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     */
    public void login(String email, String password) {
        // Appelle le service pour effectuer la logique de connexion.
        userRepository.login(email, password, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // Met à jour l'état de loginResult en cas de succès.
                loginResult.postValue("success");
            }

            @Override
            public void onFailure(String message) {
                // Met à jour l'état de loginResult avec un message d'erreur.
                loginResult.postValue(message);
            }
        });
    }

    /**
     * Lance la mise à jour des infos texte
     * @param user L'objet utilisateur contenant les informations à mettre à jour.
     */
    public void updateProfile(User user) {
        userRepository.updateProfileData(user, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                updateResult.postValue("success");
                // On met à jour le profil local pour que les autres écrans soient au courant
                userProfile.postValue(user);
            }

            @Override
            public void onFailure(String message) {
                updateResult.postValue(message);
            }
        });
    }

    /**
     * Événement déclenché par l'UI pour demander l'inscription d'un nouvel utilisateur AVEC image.
     * @param user L'objet utilisateur contenant les informations à enregistrer.
     * @param password Le mot de passe pour le nouveau compte.
     * @param localUri L'URI locale de l'image à uploader vers Firebase Storage.
     */
    public void register(User user, String password, Uri localUri) {
        // Appelle la version du Repository qui inclut l'URI pour l'upload.
        userRepository.register(user, password, localUri, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // Met à jour l'état de registerResult en cas de succès.
                registerResult.postValue("success");
            }

            @Override
            public void onFailure(String message) {
                // Met à jour l'état de registerResult avec un message d'erreur.
                // Ce message contiendra l'erreur d'upload si c'est le cas.
                registerResult.postValue(message);
            }
        });
    }

    /**
     * Événement déclenché par l'UI pour demander l'inscription d'un nouvel utilisateur SANS image.
     * @param user L'objet utilisateur contenant les informations à enregistrer.
     * @param password Le mot de passe pour le nouveau compte.
     */
    public void register(User user, String password) {
        // Appelle la version du Repository sans URI (qui délègue au Repository.register(..., null, ...))
        userRepository.register(user, password, new UserRepository.AuthCallback() {
            // ... (Logique onSuccess/onFailure existante)
            @Override
            public void onSuccess() {
                registerResult.postValue("success");
            }
            @Override
            public void onFailure(String message) {
                registerResult.postValue(message);
            }
        });
    }

    /**
     * Récupère le profil complet de l'utilisateur connecté.
     */
    public void fetchUserProfile() {
        userRepository.getUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                // Met à jour l'état du profil.
                userProfile.postValue(user);
            }

            @Override
            public void onFailure(String message) {
                // Gère l'erreur de récupération.
                profileError.postValue(message);
            }
        });
    }

    /**
     * Déclenche le renvoi de l'e-mail de vérification.
     */
    public void resendVerificationEmail(VerificationCallback callback) {
        // Le ViewModel appelle le Repository, en passant le AuthCallback
        userRepository.sendVerificationEmail(new UserRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // Le Repository réussit, le ViewModel informe l'UI via VerificationCallback
                callback.onSuccess();
            }

            @Override
            public void onFailure(String message) {
                // Le Repository échoue, le ViewModel informe l'UI via VerificationCallback
                callback.onFailure(message);
            }
        });
    }

    /**
     * Déclenche la déconnexion de l'utilisateur.
     */
    public void logout() {
        userRepository.logout();

        // Met à jour l'état de connexion. Les observateurs seront notifiés.
        // L'UI peut utiliser cet état pour naviguer.
        isLoggedIn.postValue(false);
    }
}
