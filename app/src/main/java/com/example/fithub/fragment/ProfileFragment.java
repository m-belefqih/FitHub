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

import com.example.fithub.R;
import com.example.fithub.activity.LoginActivity;
import com.example.fithub.databinding.FragmentProfileBinding;
import com.example.fithub.model.User;
import com.example.fithub.utils.SnackbarUtils;
import com.example.fithub.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel partagé avec MainActivity
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        observeViewModel();
        setupLogout();

        // Charger le profil
        userViewModel.fetchUserProfile();
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

        String description = user.getDescription();
        binding.textBio.setText(
                (description != null && !description.isEmpty())
                        ? description
                        : "No description set."
        );

        String imageUriString = user.getImage();

        if (imageUriString != null && !imageUriString.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                binding.profileImage.setImageURI(imageUri);

                requireContext().getContentResolver()
                        .takePersistableUriPermission(
                                imageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

            } catch (Exception e) {
                Log.e(TAG, "Image load failed", e);
                binding.profileImage.setImageResource(R.drawable.userprofile);
            }
        } else {
            binding.profileImage.setImageResource(R.drawable.userprofile);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // éviter memory leaks
    }
}