package com.example.fithub.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithub.R;
import com.example.fithub.activity.MainActivity;
import com.example.fithub.adapter.FoodAdapter;
import com.example.fithub.model.Food;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Food> foodList;
    private MaterialButton btnConfirmSelection;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recyclerViewFood);
        btnConfirmSelection = view.findViewById(R.id.btnConfirmSelection);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        foodList = loadFoodsFromJson();

        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);

        btnConfirmSelection.setOnClickListener(v -> {
            List<Food> selectedFoods = adapter.getSelectedFoods();
            if (selectedFoods.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one food", Toast.LENGTH_SHORT).show();
            } else {
                addSelectedFoodsToDailyCalories(selectedFoods);
            }
        });

        return view;
    }

    private void addSelectedFoodsToDailyCalories(List<Food> selectedFoods) {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        int totalCalories;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            totalCalories = selectedFoods.stream().mapToInt(Food::getCalories).sum();
        } else {
            totalCalories = 0;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("calories", FieldValue.increment(totalCalories));

        db.collection("users").document(userId)
                .collection("daily_data").document(today)
                .set(update, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Added " + totalCalories + " kcal to your day!", Toast.LENGTH_SHORT).show();
                    // Go back to Home Fragment
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).replaceFragment(new HomeFragment());
                    }
                });
    }

    private List<Food> loadFoodsFromJson() {
        List<Food> foods = new ArrayList<>();
        String json;
        try {
            InputStream is = getContext().getAssets().open("foods.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                foods.add(new Food(
                        obj.getString("name"),
                        obj.getInt("calories"),
                        obj.getString("image"),
                        obj.getDouble("protein"),
                        obj.getDouble("fat"),
                        obj.getDouble("carbs"),
                        obj.getString("servingSizeUnit")
                ));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return foods;
    }
}