package com.example.fithub.utils;

import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.Snackbar.Callback;
import com.example.fithub.R;

public class SnackbarUtils {

    private static final String ACTION_CLOSE = "CLOSE";

    // ---------------------------------------------------------------------------------
    // 1. MÉTHODE DE BASE pour la Création et le Style (Privée)
    // ---------------------------------------------------------------------------------

    private static Snackbar makeSnackbar(View view, String message, int duration, int backgroundColorResId) {
        Snackbar snackbar = Snackbar.make(view, message, duration);

        // Appliquer la couleur de fond
        snackbar.setBackgroundTint(ContextCompat.getColor(view.getContext(), backgroundColorResId));

        // Assurer que le texte principal est lisible (texte blanc)
        snackbar.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));

        return snackbar;
    }

    // ---------------------------------------------------------------------------------
    // 2. Méthode pour l'Échec/Erreur (Affichage simple + Bouton CLOSE)
    // ---------------------------------------------------------------------------------

    /**
     * Affiche un Snackbar d'erreur (Rouge) avec action de fermeture 'CLOSE'.
     * Utilise LENGTH_LONG pour laisser le temps de lire le message d'erreur.
     */
    public static void showErrorSnackbar(View view, String message) {
        Snackbar snackbar = makeSnackbar(view, message, Snackbar.LENGTH_LONG, R.color.error_red);

        // Ajouter l'action de fermeture (manuelle)
        snackbar.setAction(ACTION_CLOSE, v -> snackbar.dismiss());

        snackbar.show();
    }

    // ---------------------------------------------------------------------------------
    // 3. Méthode pour le Succès avec Navigation (Pas de bouton CLOSE)
    // ---------------------------------------------------------------------------------

    /**
     * Affiche un Snackbar de Succès (Vert) et exécute une action (Runnable) après sa disparition
     * pour permettre la navigation.
     * @param view Vue parente.
     * @param message Le message à afficher.
     * @param action Le Runnable à exécuter après la disparition du Snackbar (ex: navigation).
     */
    public static void showSuccessSnackbarAndNavigate(View view, String message, Runnable action) {
        // Utilisation de LENGTH_SHORT pour minimiser le délai de navigation
        Snackbar snackbar = makeSnackbar(view, message, Snackbar.LENGTH_SHORT, R.color.success_green);

        // AUCUN BOUTON CLOSE AJOUTÉ ICI

        // Ajouter un Callback pour détecter la fermeture (naturelle ou par swipe)
        snackbar.addCallback(new Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);

                // Exécuter l'action (navigation) après la disparition
                if (action != null) {
                    action.run();
                }
            }
        });

        snackbar.show();
    }

    // ---------------------------------------------------------------------------------
    // 4. Message d'Information Simple (Sans Couleur Custom ni Bouton)
    // ---------------------------------------------------------------------------------

    /**
     * Affiche un message d'information simple (utilise le style du thème, sans couleur custom).
     */
    public static void showInfoSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}