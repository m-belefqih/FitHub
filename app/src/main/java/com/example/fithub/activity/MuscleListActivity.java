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

public class MuscleListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscle_list);

        RecyclerView rv = findViewById(R.id.rvMuscles);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 1. Get the list using the updated DataRepository (passing 'this' context)
        List<Model.Muscle> muscleList = DataRepository.getMuscles(this);

        // 2. Set the Adapter with the Click Listener
        MuscleAdapter adapter = new MuscleAdapter(muscleList, muscle -> {
            // This code runs when you click a card
            Intent intent = new Intent(MuscleListActivity.this, ExerciseListActivity.class);
            intent.putExtra("MUSCLE_ID", muscle.id);
            intent.putExtra("MUSCLE_NAME", muscle.name);
            startActivity(intent);
        });

        rv.setAdapter(adapter);
    }

    // --- Static Adapter ---
    static class MuscleAdapter extends RecyclerView.Adapter<MuscleAdapter.ViewHolder> {
        private List<Model.Muscle> list;
        private OnItemClick onItemClick;

        public interface OnItemClick { void onClick(Model.Muscle muscle); }

        public MuscleAdapter(List<Model.Muscle> list, OnItemClick onItemClick) {
            this.list = list;
            this.onItemClick = onItemClick;
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_muscle, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Model.Muscle item = list.get(position);
            holder.tvName.setText(item.name);

            // UPDATED: Convert String image name (from JSON) to Drawable ID
            int resId = DataRepository.getResId(holder.itemView.getContext(), item.imageName);
            if (resId != 0) {
                holder.img.setImageResource(resId);
            } else {
                holder.img.setImageResource(R.drawable.ic_launcher_background);
            }

            // CRITICAL: Set the click listener on the entire item view
            holder.itemView.setOnClickListener(v -> onItemClick.onClick(item));
        }

        @Override public int getItemCount() { return list.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageView img;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvMuscleName);
                img = itemView.findViewById(R.id.imgMuscle);
            }
        }
    }
}