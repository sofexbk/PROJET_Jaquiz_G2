package com.example.javaquiz.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QuizDatabaseHelper extends SQLiteOpenHelper {

    public QuizDatabaseHelper(Context context) {
        super(context, "quiz_progression.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE QuizResults (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "quiz_mode TEXT," +
                "score INTEGER," +
                "time_taken INTEGER," +
                "date TEXT," +
                "category TEXT)";
        db.execSQL(createTableQuery);
    }
    public Cursor getCategoryStatsForPracticeMode() {
        SQLiteDatabase db = this.getReadableDatabase();
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
        return db.rawQuery(query, null);
    }
    public long saveQuizResult(String quizMode, int score, long timeTaken, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_mode", quizMode);
        values.put("score", score);
        values.put("time_taken", timeTaken);
        values.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put("category", category);

        // db.insert retourne l'ID de la nouvelle ligne ou -1 si l'insertion a échoué
        long newRowId = db.insert("QuizResults", null, values);
        db.close();
        return newRowId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS QuizResults");
        onCreate(db);
    }
}
