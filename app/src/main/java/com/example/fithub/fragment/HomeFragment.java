package com.example.fithub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fithub.R;
import com.example.fithub.activity.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView tvBmiValue, tvBmiStatus, tvHeightValue, tvWeightValue, tvConsumedCalories;
    private MaterialButton btnHeightMinus, btnHeightPlus, btnWeightMinus, btnWeightPlus, btnAddFood, btnResetCalories;
    private ImageView bmiArrow;
    private LinearLayout bmiScaleLayout;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;

    private double height = 170.0;
    private double weight = 70.0;
    private int calories = 0;

    private ListenerRegistration profileListener;
    private ListenerRegistration caloriesListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        initFirebase();
        setupListeners();
        startRealTimeUpdates();

        return view;
    }

    private void initViews(View view) {
        tvBmiValue = view.findViewById(R.id.tvBmiValue);
        tvBmiStatus = view.findViewById(R.id.tvBmiStatus);
        tvHeightValue = view.findViewById(R.id.tvHeightValue);
        tvWeightValue = view.findViewById(R.id.tvWeightValue);
        tvConsumedCalories = view.findViewById(R.id.tvConsumedCalories);
        bmiArrow = view.findViewById(R.id.bmiArrow);
        bmiScaleLayout = view.findViewById(R.id.bmiScaleLayout);

        btnHeightMinus = view.findViewById(R.id.btnHeightMinus);
        btnHeightPlus = view.findViewById(R.id.btnHeightPlus);
        btnWeightMinus = view.findViewById(R.id.btnWeightMinus);
        btnWeightPlus = view.findViewById(R.id.btnWeightPlus);
        btnAddFood = view.findViewById(R.id.btnAddFood);
        btnResetCalories = view.findViewById(R.id.btnResetCalories);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }
    }

    private void setupListeners() {
        btnHeightPlus.setOnClickListener(v -> {
            height += 1.0;
            saveToFirebase();
        });

        btnHeightMinus.setOnClickListener(v -> {
            if (height > 0) height -= 1.0;
            saveToFirebase();
        });

        btnWeightPlus.setOnClickListener(v -> {
            weight += 0.1;
            saveToFirebase();
        });

        btnWeightMinus.setOnClickListener(v -> {
            if (weight > 0.1) weight -= 0.1;
            saveToFirebase();
        });

        btnAddFood.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new FoodFragment());
            }
        });

        btnResetCalories.setOnClickListener(v -> resetDailyCalories());
    }

    private void resetDailyCalories() {
        if (userId == null) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Map<String, Object> data = new HashMap<>();
        data.put("calories", 0);

        db.collection("users").document(userId).collection("daily_data").document(today)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Calories reset!", Toast.LENGTH_SHORT).show();
                });
    }

    private void startRealTimeUpdates() {
        if (userId == null) return;

        // Real-time listener for profile (Height/Weight)
        profileListener = db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null || documentSnapshot == null || !documentSnapshot.exists()) return;

                    Double h = documentSnapshot.getDouble("height");
                    Double w = documentSnapshot.getDouble("weight");
                    if (h != null) height = h;
                    if (w != null) weight = w;
                    updateUI();
                });

        // Real-time listener for calories
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        caloriesListener = db.collection("users").document(userId).collection("daily_data").document(today)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) return;
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Long cal = documentSnapshot.getLong("calories");
                        calories = (cal != null) ? cal.intValue() : 0;
                    } else {
                        calories = 0;
                    }
                    tvConsumedCalories.setText(String.valueOf(calories));
                });
    }

    private void updateUI() {
        tvHeightValue.setText(String.format(Locale.US, "%.0f CM", height));
        tvWeightValue.setText(String.format(Locale.US, "%.1f KG", weight));

        double bmi = calculateBmi();
        tvBmiValue.setText(String.format(Locale.US, "%.2f", bmi));

        String category;
        int categoryIndex;
        if (bmi < 18.5) {
            category = "Underweight range";
            categoryIndex = 0;
        } else if (bmi < 25) {
            category = "Healthy Weight range";
            categoryIndex = 1;
        } else if (bmi < 30) {
            category = "Overweight range";
            categoryIndex = 2;
        } else {
            category = "Obese range";
            categoryIndex = 3;
        }

        tvBmiStatus.setText(category);
        updateBmiArrow(categoryIndex);
    }

    private void updateBmiArrow(int categoryIndex) {
        if (bmiArrow == null || bmiScaleLayout == null) return;
        bmiArrow.setVisibility(View.VISIBLE);
        bmiArrow.post(() -> {
            float totalWidth = bmiScaleLayout.getWidth();
            float step = totalWidth / 4;
            float targetX = (step * categoryIndex) + (step / 2) - (bmiArrow.getWidth() / 2f);
            bmiArrow.animate().translationX(targetX).setDuration(500).start();
        });
    }

    private double calculateBmi() {
        if (height <= 0) return 0;
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }

    private void saveToFirebase() {
        if (userId == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("height", height);
        data.put("weight", weight);

        db.collection("users").document(userId).update(data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (profileListener != null) profileListener.remove();
        if (caloriesListener != null) caloriesListener.remove();
    }
}