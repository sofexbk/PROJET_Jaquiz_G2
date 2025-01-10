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
    public Cursor getCategoryStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT category, " +
                "MAX(score) AS best_score, " +
                "(SELECT score FROM QuizResults r2 " +
                " WHERE r2.category = r1.category " +
                " ORDER BY date DESC LIMIT 1) AS last_score " +
                "FROM QuizResults r1 " +
                "GROUP BY category";
        return db.rawQuery(query, null);
    }

    public void saveQuizResult(String quizMode, int score, long timeTaken, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_mode", quizMode);
        values.put("score", score);
        values.put("time_taken", timeTaken);
        values.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put("category", category);
        db.insert("QuizResults", null, values);
    }

    // Méthode pour récupérer tous les résultats
    public Cursor getQuizResults() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("QuizResults", null, null, null, null, null, "date DESC");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS QuizResults");
        onCreate(db);
    }
}
