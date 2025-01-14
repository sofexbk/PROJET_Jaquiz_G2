package com.example.javaquiz.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cette classe gère la base de données SQLite pour stocker les résultats de quiz en mode pratique.
 * Elle permet de créer la base de données, insérer des résultats de quiz,
 * et récupérer des statistiques basées sur les catégories de quiz en mode pratique.
 */
public class QuizDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Constructeur de la classe QuizDatabaseHelper.
     * Initialise la base de données avec le nom "quiz_progression.db" et la version 1.
     *
     * @param context Le contexte de l'application ou de l'activité qui appelle ce constructeur.
     */
    public QuizDatabaseHelper(Context context) {
        super(context, "quiz_progression.db", null, 1);
    }

    /**
     * Méthode appelée lors de la création de la base de données.
     * Crée une table "QuizResults" pour stocker les résultats des quiz.
     *
     * @param db La base de données SQLite.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Requête SQL pour créer la table QuizResults
        String createTableQuery = "CREATE TABLE QuizResults (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "quiz_mode TEXT," +
                "score INTEGER," +
                "time_taken INTEGER," +
                "date TEXT," +
                "category TEXT)";
        db.execSQL(createTableQuery);
    }

    /**
     * Récupère les statistiques des résultats des quiz en mode "Practice" pour chaque catégorie.
     * Les statistiques incluent le meilleur score et le dernier score pour chaque catégorie.
     *
     * @return Un curseur contenant les résultats des statistiques par catégorie.
     */
    public Cursor getCategoryStatsForPracticeMode() {
        // Récupère une base de données en lecture
        SQLiteDatabase db = this.getReadableDatabase();

        // Requête SQL pour récupérer les meilleures performances et le dernier score par catégorie
        String query = "SELECT category, " +
                "MAX(CASE WHEN category = 'beginner' THEN score END) as beginner_best_score, " +
                "(SELECT score FROM QuizResults r2 " +
                " WHERE r2.category = 'beginner' AND r2.quiz_mode = 'Practice' " +
                " ORDER BY date DESC LIMIT 1) as beginner_last_score, " +
                "MAX(CASE WHEN category = 'intermediate' THEN score END) as intermediate_best_score, " +
                "(SELECT score FROM QuizResults r2 " +
                " WHERE r2.category = 'intermediate' AND r2.quiz_mode = 'Practice' " +
                " ORDER BY date DESC LIMIT 1) as intermediate_last_score, " +
                "MAX(CASE WHEN category = 'advanced' THEN score END) as advanced_best_score, " +
                "(SELECT score FROM QuizResults r2 " +
                " WHERE r2.category = 'advanced' AND r2.quiz_mode = 'Practice' " +
                " ORDER BY date DESC LIMIT 1) as advanced_last_score " +
                "FROM QuizResults r1 " +
                "WHERE r1.quiz_mode = 'Practice'";

        // Exécute la requête et retourne un curseur avec les résultats
        return db.rawQuery(query, null);
    }

    /**
     * Enregistre un résultat de quiz dans la base de données.
     *
     * @param quizMode Le mode du quiz (par exemple, "Practice", "Exam").
     * @param score Le score obtenu dans le quiz.
     * @param timeTaken Le temps pris pour compléter le quiz, en millisecondes.
     * @param category La catégorie du quiz (par exemple, "beginner", "intermediate", "advanced").
     * @return L'ID de la nouvelle ligne insérée dans la base de données, ou -1 en cas d'échec.
     */
    public long saveQuizResult(String quizMode, int score, long timeTaken, String category) {
        // Récupère une base de données en écriture
        SQLiteDatabase db = this.getWritableDatabase();

        // Crée un objet ContentValues pour stocker les données du quiz
        ContentValues values = new ContentValues();
        values.put("quiz_mode", quizMode);
        values.put("score", score);
        values.put("time_taken", timeTaken);
        values.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put("category", category);

        // Insère les valeurs dans la table QuizResults et retourne l'ID de la nouvelle ligne
        long newRowId = db.insert("QuizResults", null, values);

        // Ferme la base de données
        db.close();

        // Retourne l'ID de la nouvelle ligne insérée
        return newRowId;
    }

    /**
     * Méthode appelée lors de la mise à jour de la base de données (version supérieure).
     * Supprime la table existante et crée une nouvelle version de la base de données.
     *
     * @param db La base de données SQLite.
     * @param oldVersion La version précédente de la base de données.
     * @param newVersion La nouvelle version de la base de données.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprime la table QuizResults si elle existe
        db.execSQL("DROP TABLE IF EXISTS QuizResults");

        // Recrée la table
        onCreate(db);
    }
}
