package com.example.fithub.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fithub.R;
import com.example.fithub.model.Model;
import com.example.fithub.repository.DataRepository;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscle_list);

        String muscleId = getIntent().getStringExtra("MUSCLE_ID");
        String muscleName = getIntent().getStringExtra("MUSCLE_NAME");

        TextView title = findViewById(R.id.tvHeader);
        title.setText(muscleName);

        RecyclerView rv = findViewById(R.id.rvMuscles);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 1. Find the muscle in the JSON data
        List<Model.Muscle> allMuscles = DataRepository.getMuscles(this);
        List<Model.Exercise> exercises = null;

        for (Model.Muscle m : allMuscles) {
            if (m.id.equals(muscleId)) {
                exercises = m.exerciseList;
                break;
            }
        }

        // 2. Set Adapter
        if (exercises != null) {
            rv.setAdapter(new ExerciseAdapter(exercises, exercise -> {
                Intent intent = new Intent(ExerciseListActivity.this, TrainingActivity.class);
                intent.putExtra("EXERCISE", exercise);
                startActivity(intent);
            }));
        }
    }

    static class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
        private List<Model.Exercise> list;
        private OnItemClick onItemClick;

        public interface OnItemClick { void onClick(Model.Exercise exercise); }

        public ExerciseAdapter(List<Model.Exercise> list, OnItemClick onItemClick) {
            this.list = list;
            this.onItemClick = onItemClick;
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // USE THE NEW LAYOUT item_exercise
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Model.Exercise item = list.get(position);

            // Set Text Data
            holder.tvName.setText(item.name);
            holder.tvDifficulty.setText(item.difficulty);
            holder.tvCalories.setText(item.calories);

            // --- DYNAMIC COLOR LOGIC START ---

            // 1. Get the background drawable of the TextView
            android.graphics.drawable.Drawable background = holder.tvDifficulty.getBackground();

            // 2. Determine color based on difficulty text
            int colorCode;
            switch (item.difficulty.toLowerCase()) {
                case "easy":
                    colorCode = android.graphics.Color.parseColor("#BCFF31"); // Your Neon Green
                    break;
                case "medium":
                    colorCode = android.graphics.Color.parseColor("#FFA500"); // Orange
                    break;
                case "hard":
                    colorCode = android.graphics.Color.parseColor("#E83937"); // Red
                    break;
                default:
                    colorCode = android.graphics.Color.GRAY;
            }

            // 3. Apply the color filter to the background shape
            // We wrap it to ensure it doesn't affect other items sharing the same drawable resource
            androidx.core.graphics.drawable.DrawableCompat.setTint(
                    androidx.core.graphics.drawable.DrawableCompat.wrap(background),
                    colorCode
            );

            // --- DYNAMIC COLOR LOGIC END ---

            // Image Loading
            int resId = DataRepository.getResId(holder.itemView.getContext(), item.imageName);
            if (resId != 0) holder.img.setImageResource(resId);

            holder.itemView.setOnClickListener(v -> onItemClick.onClick(item));
        }

        @Override public int getItemCount() { return list.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDifficulty, tvCalories;
            ImageView img;
            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvExerciseName);
                tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
                tvCalories = itemView.findViewById(R.id.tvCalories);
                img = itemView.findViewById(R.id.imgExercise);
            }
        }
    }
}