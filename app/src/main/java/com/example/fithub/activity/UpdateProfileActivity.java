package com.example.fithub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.fithub.R;
import com.example.fithub.databinding.ActivityUpdateProfileBinding;
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

public class UpdateProfileActivity extends AppCompatActivity {
    private ActivityUpdateProfileBinding binding;
    private UserViewModel userViewModel;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setupObservers();

        // Clic sur le bouton de sauvegarde
        binding.btnUpdate.setOnClickListener(v -> {
            if (validateFields()) {
                updateUserFromUI();
                userViewModel.updateProfile(currentUser);
            }
        });

        // Clic sur la date pour ouvrir le calendrier
        binding.etBirthDate.setOnClickListener(v -> showDatePicker());

        binding.btnBack.setOnClickListener(v -> finish());

        // Charger les infos
        userViewModel.fetchUserProfile();
    }

    private void setupObservers() {
        userViewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                this.currentUser = user;
                // Pré-remplissage des champs
                binding.etUsername.setText(user.getUsername());
                binding.etDescription.setText(user.getDescription());
                binding.etWeight.setText(String.valueOf((int) user.getWeight()));
                binding.etHeight.setText(String.valueOf((int) user.getHeight()));
                binding.etBirthDate.setText(user.getBirthDate());

                if ("Male".equals(user.getGender())) binding.rbMale.setChecked(true);
                else binding.rbFemale.setChecked(true);

                // Charger l'image de profil
                String imageUrl = user.getImage();

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    if (imageUrl.contains("drive.google.com")) {
                        // Cas Google Drive
                        String directUrl = convertDriveUrl(imageUrl);
                        Glide.with(this)
                                .load(directUrl)
                                .placeholder(R.drawable.coach) // Image affichée pendant le chargement
                                .error(R.drawable.coach)       // Image affichée en cas d'erreur
                                .into(binding.profileImage);
                    } else {
                        // Cas URL classique ou Firebase Storage
                        Glide.with(this)
                                .load(imageUrl)
                                .error(R.drawable.coach)
                                .into(binding.profileImage);
                    }
                } else {
                    // Aucune image définie
                    binding.profileImage.setImageResource(R.drawable.coach);
                }
            }
        });

        userViewModel.getUpdateResult().observe(this, result -> {
            if(result.equals("success")){
                // Afficher le Snackbar de Succès
                SnackbarUtils.showSuccessSnackbarAndNavigate(binding.getRoot(), "Profile updated successfully!", () -> {
                    // Cette partie s'exécute APRÈS la disparition du Snackbar
                    finish();
                });
            } else {
                // Le cas d'erreur
                SnackbarUtils.showErrorSnackbar(binding.getRoot(), "Error : " + result);
            }
        });
    }

    // Google Drive → Direct image
    private String convertDriveUrl(String url) {
        return url.replace("file/d/", "uc?export=view&id=")
                .replace("/view?usp=sharing", "");
    }

    private void updateUserFromUI() {
        currentUser.setUsername(binding.etUsername.getText().toString().trim());
        currentUser.setDescription(binding.etDescription.getText().toString().trim());
        currentUser.setWeight(Double.parseDouble(binding.etWeight.getText().toString()));
        currentUser.setHeight(Double.parseDouble(binding.etHeight.getText().toString()));
        currentUser.setBirthDate(binding.etBirthDate.getText().toString());
        currentUser.setGender(binding.rbMale.isChecked() ? "Male" : "Female");
    }

    private void showDatePicker() {
        // Créer le sélecteur de date (Docked date picker)
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birth Date")
                // Utiliser la date actuelle comme sélection initiale (en millisecondes UTC)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build();
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        // Nous écoutons le clic sur l'icône elle-même.
        binding.tilBirthDate.setEndIconOnClickListener(v -> {
            if (!datePicker.isAdded()) {
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });


        // Gérer le résultat lorsque l'utilisateur clique sur OK
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
    }

    private boolean validateFields() {
        if (binding.etUsername.getText().toString().trim().isEmpty()) {
            binding.tilUsername.setError("Le nom est requis");
            return false;
        }
        return true;
    }
}