package com.example.fithub.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub.R;

import static com.example.fithub.model.Model.loadJSONFromAsset;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithub.model.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class WorkoutListActivity extends AppCompatActivity {

    RecyclerView rvWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        rvWorkouts = findViewById(R.id.rvWorkouts);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(this));

        // Load Data
        String jsonFileString = loadJSONFromAsset(getApplicationContext(), "workouts.json");
        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<Model.Workout>>() {
        }.getType();
        List<Model.Workout> workouts = gson.fromJson(jsonFileString, listUserType);

        // Set Adapter
        WorkoutAdapter adapter = new WorkoutAdapter(workouts, this::onWorkoutSelected);
        rvWorkouts.setAdapter(adapter);
    }

    private void onWorkoutSelected(Model.Workout workout) {
        Intent intent = new Intent(this, WorkoutSessionActivity.class);
        intent.putExtra("WORKOUT_DATA", workout);
        startActivity(intent);
    }

    // --- FIX: Added 'static' keyword here ---
    // ... inside WorkoutListActivity.java ...

    static class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

        private List<Model.Workout> list;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(Model.Workout item);
        }

        public WorkoutAdapter(List<Model.Workout> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_workout_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Model.Workout workout = list.get(position);

            // 1. Set Text Data
            holder.tvTitle.setText(workout.title);
            holder.tvLevel.setText(workout.level);
            holder.tvDesc.setText(workout.description);
            holder.tvDuration.setText(workout.estDuration);
            holder.tvCalories.setText(workout.estCalories);

            // 2. Dynamic Image Loading
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    workout.imageName, "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.imgCover.setImageResource(resId);
            }

            // 3. Optional: Color code the level badge
            if ("Hard".equalsIgnoreCase(workout.level)) {
                holder.tvLevel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#e83937"))); // Red
            } else if ("Beginner".equalsIgnoreCase(workout.level)) {
                holder.tvLevel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50"))); // Green
            } else if ("Intermediate".equalsIgnoreCase(workout.level)) {
                holder.tvLevel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#BCFF31"))); // Primary
            } else { // Expert
                holder.tvLevel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFA500"))); // Orange
            }

            holder.itemView.setOnClickListener(v -> listener.onItemClick(workout));
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        // --- UPDATED VIEW HOLDER ---
        static class ViewHolder extends RecyclerView.ViewHolder {
            // Declare all the new views here
            TextView tvTitle, tvLevel, tvDesc, tvDuration, tvCalories;
            ImageView imgCover;

            public ViewHolder(View itemView) {
                super(itemView);
                // Connect them to the XML IDs from item_workout_card.xml
                tvTitle = itemView.findViewById(R.id.tvWorkoutTitle);
                tvLevel = itemView.findViewById(R.id.tvLevelBadge);    // Maps to XML id: tvLevelBadge
                tvDesc = itemView.findViewById(R.id.tvDescription);    // Maps to XML id: tvDescription
                tvDuration = itemView.findViewById(R.id.tvDuration);   // Maps to XML id: tvDuration
                tvCalories = itemView.findViewById(R.id.tvCalorieTotal); // Maps to XML id: tvCalorieTotal
                imgCover = itemView.findViewById(R.id.imgWorkoutCover);
            }
        }
    }
}