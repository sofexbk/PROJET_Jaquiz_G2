package com.example.javaquiz.Utils;

import com.example.javaquiz.Models.Question;

import java.util.List;
import java.util.Map;

public class QuizData {
    private Map<String, List<Question>> categories;
    public Map<String, List<Question>> getCategories() {
        return categories;
    }
}
