package com.example.javaquiz.Utils;

import android.content.Context;
import android.util.Log;
import java.io.InputStream;
import java.io.IOException;

/**
 * La classe loadQuizData fournit une méthode utilitaire pour lire des fichiers JSON
 * depuis le répertoire "raw" des ressources Android.
 */
public class loadQuizData {

    private static final String TAG = "loadQuizData";

    /**
     * Lit un fichier JSON depuis le répertoire "raw" et le retourne sous forme de chaîne.
     *
     * @param context Le contexte de l'application ou de l'activité, utilisé pour accéder aux ressources.
     * @param resourceId L'identifiant de la ressource (fichier JSON) située dans le dossier "raw".
     * @return Une chaîne représentant le contenu du fichier JSON, ou {@code null} en cas d'erreur.
     */
    public static String readJsonFromRaw(Context context, int resourceId) {
        InputStream inputStream = null;
        try {
            // Ouvre le fichier spécifié dans le dossier "raw"
            inputStream = context.getResources().openRawResource(resourceId);

            // Crée un tampon pour lire tout le contenu du fichier
            byte[] buffer = new byte[inputStream.available()];

            // Lit les données dans le tampon
            inputStream.read(buffer);

            // Convertit les données lues en chaîne avec l'encodage UTF-8
            return new String(buffer, "UTF-8");

        } catch (IOException e) {
            // Gère les erreurs de lecture du fichier (fichier manquant, problème d'accès, etc.)
            Log.e(TAG, "Erreur de lecture du fichier JSON", e);
        } catch (Exception e) {
            // Gère toute autre exception inattendue
            Log.e(TAG, "Erreur inattendue", e);
        } finally {
            // Assurez-vous que le flux d'entrée est fermé dans le bloc finally
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Erreur lors de la fermeture du flux d'entrée", e);
                }
            }
        }

        // Retourne null en cas d'erreur
        return null;
    }
}
