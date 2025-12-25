package com.example.fithub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fithub.R;
import com.example.fithub.model.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private List<Food> selectedFoods = new ArrayList<>();

    public FoodAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.foodName.setText(food.getName());
        holder.foodServing.setText(food.getServingSizeUnit() + " serving");
        holder.foodCalories.setText(food.getCalories() + " kcal");
        
        String macros = String.format(Locale.getDefault(), "Protein: %.1fg | Carbs: %.1fg | Fat: %.1fg",
                food.getProtein(), food.getCarbs(), food.getFat());
        holder.foodMacros.setText(macros);
        
        String imagePath = food.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("http")) {
                Glide.with(holder.itemView.getContext())
                        .load(imagePath)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(holder.foodImage);
            } else {
                String resourceName = imagePath.contains(".") ? imagePath.substring(0, imagePath.lastIndexOf('.')) : imagePath;
                int resId = holder.itemView.getContext().getResources().getIdentifier(resourceName, "drawable", holder.itemView.getContext().getPackageName());
                
                if (resId != 0) {
                    Glide.with(holder.itemView.getContext())
                            .load(resId)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(holder.foodImage);
                } else {
                    Glide.with(holder.itemView.getContext())
                            .load("file:///android_asset/" + imagePath)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(holder.foodImage);
                }
            }
        } else {
            holder.foodImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.foodCheckBox.setOnCheckedChangeListener(null);
        holder.foodCheckBox.setChecked(selectedFoods.contains(food));

        holder.foodCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedFoods.contains(food)) {
                    selectedFoods.add(food);
                }
            } else {
                selectedFoods.remove(food);
            }
        });
    }

    public List<Food> getSelectedFoods() {
        return selectedFoods;
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodServing, foodCalories, foodMacros;
        CheckBox foodCheckBox;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodServing = itemView.findViewById(R.id.foodServing);
            foodCalories = itemView.findViewById(R.id.foodCalories);
            foodMacros = itemView.findViewById(R.id.foodMacros);
            foodCheckBox = itemView.findViewById(R.id.foodCheckBox);
        }
    }
}