package com.example.fithub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fithub.databinding.ActivityLoginBinding;
import com.example.fithub.utils.SnackbarUtils;
import com.example.fithub.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    // Instance de la classe de liaison de vues (View Binding) générée pour 'activity_login.xml'.
    // Elle permet d'accéder aux vues de la mise en page de manière sécurisée et concise.
    private ActivityLoginBinding binding;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // --- NOUVEL OBSERVATEUR : VÉRIFICATION DE SESSION AU DÉMARRAGE ---
        userViewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                // En cas de sesssion found
                // L'utilisateur a une session persistante valide. Naviguer directement.
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class)); // Naviguer vers l'écran principal
                finish();
            }
            // Si isLoggedIn est faux, l'interface de connexion reste visible (comportement par défaut).
        });


        userViewModel.getLoginResult().observe(this, result -> {
            if(result.equals("success")){

                // Afficher le Snackbar de Succès
                SnackbarUtils.showSuccessSnackbarAndNavigate(binding.getRoot(), "Login successful", () -> {
                    // Cette partie s'exécute APRÈS la disparition du Snackbar
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
            } else {
                // Le cas d'erreur fonctionne car l'activité ne se termine pas
                SnackbarUtils.showErrorSnackbar(binding.getRoot(), "Email or password is incorrect");
            }
        });



        // Button of Login
        binding.btnLogin.setOnClickListener(v -> {
            // get email and password
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // if email
            if(!email.isEmpty() && !password.isEmpty()){
                userViewModel.login(email, password);
            } else {
                SnackbarUtils.showInfoSnackbar(binding.getRoot(), "Fill all fields");
            }
        });

        // to go to Register Page
        binding.tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
