package com.example.fithub.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fithub.R;
import com.example.fithub.databinding.ActivityRegisterBinding;
import com.example.fithub.model.User;
import com.example.fithub.utils.SnackbarUtils;
import com.example.fithub.viewmodel.UserViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserViewModel userViewModel;

    // URI de l'image sélectionnée
    private Uri selectedImageUri;

    // Launcher pour choisir l'image dans la galerie
    private ActivityResultLauncher<String> galleryLauncher;

    // Launcher pour demander les permissions (nécessaire si l'API est < 33)
    private ActivityResultLauncher<String> requestPermissionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observer Register Result
        userViewModel.getRegisterResult().observe(this, result -> {
            if ("success".equals(result)) {
                // Afficher le Snackbar de Succès
                SnackbarUtils.showSuccessSnackbarAndNavigate(binding.getRoot(), "Registration successful. Please verify email.", () -> {
                    // Cette partie s'exécute APRÈS la disparition du Snackbar
                    startActivity(new Intent(this, EmailVerificationActivity.class));
                    finish();
                });
            } else {
                SnackbarUtils.showErrorSnackbar(binding.getRoot(), "Registration failed:" + result);
                // show a message in the log
                Log.e("RegisterActivity", "Registration failed: " + result);
            }
        });


        /** =======================
         * GESTION DU DATE PICKER
         ========================*/
        // 1. Créer le sélecteur de date (Docked date picker)
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birth Date")
                // Utiliser la date actuelle comme sélection initiale (en millisecondes UTC)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build();


        // 2. Écouteur de Clic sur le Champ de Date
        // Nous utilisons l'EditText ET l'icône de fin (endIcon) pour ouvrir le sélecteur
        binding.etBirthDate.setOnClickListener(v -> {
            // Afficher le sélecteur de date
            if (!datePicker.isAdded()) {
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        // Nous écoutons le clic sur l'icône elle-même.
        binding.tilBirthDate.setEndIconOnClickListener(v -> {
            if (!datePicker.isAdded()) {
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });


        // 3. Gérer le résultat lorsque l'utilisateur clique sur OK
        datePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        // Le Date Picker renvoie le temps sélectionné en millisecondes UTC.

                        // Créer un calendrier pour convertir les millisecondes en Date
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.setTimeInMillis(selection);

                        // Définir le format de sortie souhaité pour Firestore : YYYY-MM-DD
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        // Si vous voulez MM/DD/YYYY, utilisez :
                        // SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

                        String formattedDate = dateFormat.format(calendar.getTime());

                        // Mettre à jour l'EditText avec la date formatée
                        binding.etBirthDate.setText(formattedDate);
                    }
                }
        );

        /** =======================
         * GESTION DE L'UPLOAD DE PHOTO
         ========================*/
        // Gère la sélection de l'image
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        // 2. Afficher l'image dans la MaterialCardView
                        // Note: Nous utilisons le MaterialCardView lui-même comme cible
                        binding.cvPhotoUpload.removeAllViews(); // Supprimer l'icône et le texte de hint

                        // Créer et configurer une ImageView pour afficher l'image sélectionnée
                        ImageView profilePicView = new ImageView(this);
                        profilePicView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                        profilePicView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        profilePicView.setImageURI(selectedImageUri);

                        binding.cvPhotoUpload.addView(profilePicView);
                    }
                }
        );

        // Gère la demande de permission (pour les API < 33)
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        galleryLauncher.launch("image/*"); // Lancer la galerie si la permission est accordée
                    } else {
                        SnackbarUtils.showInfoSnackbar(binding.getRoot(), "Permission denied. Cannot select photo.");
                    }
                }
        );

        // Écouteur de Clic sur la Photo (cvPhotoUpload)
        binding.cvPhotoUpload.setOnClickListener(v -> {
            // Vérifier la version d'Android pour savoir quelle permission demander
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
                galleryLauncher.launch("image/*"); // Pas besoin de permission READ_EXTERNAL_STORAGE
            } else {
                // Pour API < 33, nous demandons la permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        /**
         * GESTION DE L'INSCRIPTION
         */
        // Button Register
        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String weightStr = binding.etWeight.getText().toString().trim();
            String heightStr = binding.etHeight.getText().toString().trim();

            String birthDate = binding.etBirthDate.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                    || birthDate.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
                SnackbarUtils.showInfoSnackbar(binding.getRoot(), "Please fill in all fields");
                return;
            }

            double weight;
            double height;
            try {
                weight = Double.parseDouble(weightStr);
                height = Double.parseDouble(heightStr);
            } catch (NumberFormatException e) {
                SnackbarUtils.showInfoSnackbar(binding.getRoot(), "Invalid number for weight/height");
                return;
            }

            // Determine gender from radio group
            String gender = "";
            int checkedId = binding.rgGender.getCheckedRadioButtonId();
            if (checkedId == binding.rbMale.getId()) {
                gender = "Male";
            } else if (checkedId == binding.rbFemale.getId()) {
                gender = "Female";
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setBirthDate(birthDate);
            user.setWeight(weight);
            user.setHeight(height);
            user.setImage("");          // placeholder
            user.setDescription("");    // placeholder
            user.setGender(gender);
            user.setScore(0L);          // default
            user.setJoinTime(new Date()); // set join time as Date

            // Gestion de l'URI de l'image
            if (selectedImageUri != null) {
                // Appelle la NOUVELLE méthode qui déclenche l'upload vers Firebase Storage
                // userViewModel.register(user, password, selectedImageUri);

                // Appelle la méthode pour l'inscription sans image
                userViewModel.register(user, password);

            } else {
                // Appelle l'ancienne méthode pour l'inscription sans image
                // userViewModel.register(user, password);
            }
        });

        // disable register by default
        binding.btnRegister.setEnabled(false);
        binding.btnRegister.setAlpha(0.5f);

        // checkbox listener to enable/disable register button
        binding.cbAcceptTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.btnRegister.setEnabled(isChecked);
            binding.btnRegister.setAlpha(isChecked ? 1f : 0.5f);
        });

        // to go back to Login Page
        binding.btnBack.setOnClickListener(v -> {
            onBackPressed(); // behaves like the hardware back button
        });

        // to go to Login Page
        binding.tvGoLogin.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}
