package com.example.fithub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fithub.R;
import com.example.fithub.databinding.ActivityEmailVerificationBinding;
import com.example.fithub.utils.SnackbarUtils;
import com.example.fithub.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class EmailVerificationActivity extends AppCompatActivity {

    private ActivityEmailVerificationBinding binding;
    private UserViewModel userViewModel;
    private CountDownTimer countDownTimer;
    private long timeRemaining = 30000; // 30 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Afficher l'email de l'utilisateur pour l'instruction
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            String instructionText = getString(R.string.verification_instruction_base, userEmail);
            binding.tvVerificationInstruction.setText(instructionText);
        } else {
            // Rediriger si aucun utilisateur n'est connecté
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Initialiser le chronomètre
        startTimer();

        // 1. Gérer le clic du bouton "Verify Email Status"
        binding.btnVerify.setOnClickListener(v -> checkEmailVerificationStatus());

        // 2. Gérer le clic du lien "Resend code"
        binding.tvResendCode.setOnClickListener(v -> resendVerificationEmail());

        // 3. Bouton retour
        binding.btnBack.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Lance le chronomètre de 30 secondes pour le lien de renvoi.
     */
    private void startTimer() {
        binding.tvResendCode.setEnabled(false);
        binding.tvResendCode.setAlpha(0.5f);

        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("00:00");
                binding.tvResendCode.setEnabled(true);
                binding.tvResendCode.setAlpha(1.0f);
                timeRemaining = 30000; // Réinitialiser le temps pour la prochaine tentative
            }
        }.start();
    }

    /**
     * Met à jour le TextView du chronomètre.
     */
    private void updateCountDownText() {
        int seconds = (int) (timeRemaining / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "00:%02d", seconds);
        binding.tvTimer.setText(timeLeftFormatted);
    }

    /**
     * Tente de renvoyer l'e-mail de vérification.
     */
    private void resendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userViewModel.resendVerificationEmail(new UserViewModel.VerificationCallback() {
                @Override
                public void onSuccess() {

                    // Remplacer le Toast par le Snackbar avec l'action différée
                    SnackbarUtils.showSuccessSnackbarAndNavigate(
                            // Le premier argument doit être la vue racine de votre activité (obtenue via View Binding)
                            binding.getRoot(),

                            // Message de succès
                            "Verification email sent successfully.",

                            // Le Runnable contenant l'action à exécuter APRÈS la disparition du Snackbar
                            () -> {
                                startTimer(); // Redémarrer le chronomètre
                            }
                    );
                }

                @Override
                public void onFailure(String message) {
                    SnackbarUtils.showErrorSnackbar(binding.getRoot(), "Failed to send email");
                    Log.e("EmailVerificationActivity", "Failed to send verification email: " + message);
                }
            });
        } else {
            SnackbarUtils.showErrorSnackbar(binding.getRoot(), "Error: No user found..");
        }
    }

    /**
     * Vérifie l'état de l'e-mail dans Firebase et navigue si vérifié.
     */
    private void checkEmailVerificationStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        SnackbarUtils.showSuccessSnackbarAndNavigate(binding.getRoot(), "Email verified successfully!", () -> {
                            // Cette partie s'exécute APRÈS la disparition du Snackbar
                            // Redirection vers l'activité principale (Login ou Main App)
                            Intent intent = new Intent(EmailVerificationActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        SnackbarUtils.showErrorSnackbar(binding.getRoot(), "Email not yet verified. Please check your inbox.");
                    }
                } else {
                    SnackbarUtils.showErrorSnackbar(binding.getRoot(), "Failed to check status. Try again.");
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}