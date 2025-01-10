package com.example.javaquiz.Utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javaquiz.Models.CategoryStats;
import com.example.javaquiz.R;

import java.util.List;

public class CategoryStatsAdapter extends RecyclerView.Adapter<CategoryStatsAdapter.ViewHolder> {

    private final List<CategoryStats> categoryStatsList;

    public CategoryStatsAdapter(List<CategoryStats> categoryStatsList) {
        this.categoryStatsList = categoryStatsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflation du layout de chaque élément
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_stats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryStats stats = categoryStatsList.get(position);
        Log.d("Category Stats", "Displaying category: " + stats.getCategoryName());
        holder.categoryNameText.setText(stats.getCategoryName());
        holder.lastScoreText.setText("Last Score: " + stats.getLastScore());
        holder.bestScoreText.setText("Best Score: " + stats.getBestScore());
    }

    @Override
    public int getItemCount() {
        return categoryStatsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameText, lastScoreText, bestScoreText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameText = itemView.findViewById(R.id.categoryTitle);
            lastScoreText = itemView.findViewById(R.id.lastScoreText);
            bestScoreText = itemView.findViewById(R.id.bestScoreText);
        }
    }
}
