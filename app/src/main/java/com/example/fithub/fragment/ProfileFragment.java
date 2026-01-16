package com.example.fithub.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fithub.R;
import com.example.fithub.activity.CreatePostActivity;
import com.example.fithub.activity.LoginActivity;
import com.example.fithub.activity.MainActivity;
import com.example.fithub.activity.RegisterActivity;
import com.example.fithub.activity.UpdateProfileActivity;
import com.example.fithub.databinding.FragmentProfileBinding;
import com.example.fithub.model.User;
import com.example.fithub.utils.SnackbarUtils;
import com.example.fithub.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel partagé avec MainActivity
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        observeViewModel();
        setupLogout();

        // Charger le profil
        userViewModel.fetchUserProfile();

        // pour éditer le profil
        binding.iconEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UpdateProfileActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {

        // Données utilisateur
        userViewModel.getUserProfile().observe(
                getViewLifecycleOwner(),
                user -> {
                    if (user != null) {
                        displayUserProfile(user);
                    }
                }
        );

        // Erreurs
        userViewModel.getProfileError().observe(
                getViewLifecycleOwner(),
                error -> {
                    if (error != null) {
                        SnackbarUtils.showErrorSnackbar(
                                binding.getRoot(),
                                "Error loading profile"
                        );
                        Log.e(TAG, error);
                    }
                }
        );

        // Déconnexion
        userViewModel.getIsLoggedIn().observe(
                getViewLifecycleOwner(),
                loggedIn -> {
                    if (loggedIn != null && !loggedIn) {
                        SnackbarUtils.showSuccessSnackbarAndNavigate(
                                binding.getRoot(),
                                "Logged out successfully",
                                () -> {
                                    Intent intent = new Intent(
                                            requireContext(),
                                            LoginActivity.class
                                    );
                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );
                                    startActivity(intent);
                                    requireActivity().finish();
                                }
                        );
                    }
                }
        );
    }

    private void setupLogout() {
        if (binding.itemLogout != null) {
            binding.itemLogout.setOnClickListener(v -> {
                showLogoutConfirmationDialog();
            });
        }
    }

    private void displayUserProfile(User user) {

        binding.textUsername.setText(user.getUsername());

        binding.textEmail.setText(user.getEmail());

        if (user.getJoinTime() != null) {
            SimpleDateFormat sdfJoin = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String joinedDateFormatted = sdfJoin.format(user.getJoinTime());
            binding.textJoinedDate.setText("Joined " + joinedDateFormatted);
        }

        String description = user.getDescription();
        binding.textBio.setText(
                (description != null && !description.isEmpty())
                        ? description
                        : "No description set."
        );

        binding.textWeight.setText("Weight - " + String.valueOf((int) user.getWeight()) + " kg");

        binding.textHeight.setText("Height - " + String.valueOf((int) user.getHeight()) + " cm");

        binding.textGender.setText("Gender - " + user.getGender());

        String rawBirthDate = user.getBirthDate(); // "1999-10-18"
        if (rawBirthDate != null && !rawBirthDate.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(rawBirthDate);
                String formattedBirth = outputFormat.format(date);
                binding.textBirthday.setText("Birthday - " + formattedBirth);
            } catch (ParseException e) {
                binding.textBirthday.setText("Birthday - " + rawBirthDate); // Fallback en cas d'erreur
            }
        }

        loadProfileImage(user.getImage());
    }

    private void loadProfileImage(String imageUrl) {

        if (imageUrl != null
                && !imageUrl.isEmpty()
                && imageUrl.contains("drive.google.com")) {

            String directUrl = convertDriveUrl(imageUrl);

            Glide.with(requireContext())
                    .load(directUrl)
                    .error(R.drawable.coach)
                    .into(binding.profileImage);

        } else {
            binding.profileImage.setImageResource(R.drawable.coach);
        }
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_LogoutDialog
        )
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    userViewModel.logout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    // Google Drive → Direct image
    private String convertDriveUrl(String url) {
        return url.replace("file/d/", "uc?export=view&id=")
                .replace("/view?usp=sharing", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Forcer le rafraîchissement des données depuis Firebase dès qu'on revient sur l'écran
        if (userViewModel != null) {
            userViewModel.fetchUserProfile();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // éviter memory leaks
    }
}