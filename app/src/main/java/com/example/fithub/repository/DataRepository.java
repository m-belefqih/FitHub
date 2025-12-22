package com.example.fithub.repository;

import android.content.Context;

import com.example.fithub.model.Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    private static List<Model.Muscle> cachedMuscles = null;

    // Load data from assets/data.json
    public static List<Model.Muscle> getMuscles(Context context) {
        if (cachedMuscles != null) return cachedMuscles;

        cachedMuscles = new ArrayList<>();
        try {
            // 1. Read File
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            // 2. Parse JSON
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject muscleObj = jsonArray.getJSONObject(i);

                Model.Muscle muscle = new Model.Muscle(
                        muscleObj.getString("id"),
                        muscleObj.getString("name"),
                        muscleObj.optString("image", "ic_launcher_background")
                );

                // Parse Exercises inside this muscle
                JSONArray exercisesArray = muscleObj.getJSONArray("exercises");
                for (int j = 0; j < exercisesArray.length(); j++) {
                    JSONObject exObj = exercisesArray.getJSONObject(j);
                    // Inside the parsing loop...
                    muscle.exerciseList.add(new Model.Exercise(
                            exObj.getString("name"),
                            exObj.optString("image", "ic_launcher_foreground"),
                            exObj.optString("difficulty", "Medium"),
                            exObj.optString("calories", "0 kcal"),
                            exObj.optInt("duration", 60),               // Default 60s if missing
                            exObj.optString("instructions", "No instructions provided.") // Default text
                    ));
                }

                cachedMuscles.add(muscle);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cachedMuscles;
    }
    public static List<Model.Workout> getWorkouts(Context context) {
        List<Model.Workout> list = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("workouts.json"); // Load new file
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                // Parse the exercises inside this workout
                List<Model.Exercise> exercises = new ArrayList<>();
                JSONArray exArray = obj.getJSONArray("exercises");
                for(int j=0; j<exArray.length(); j++) {
                    JSONObject exObj = exArray.getJSONObject(j);
                    exercises.add(new Model.Exercise(
                            exObj.getString("name"),
                            exObj.optString("image", ""),
                            exObj.optString("difficulty", "Medium"),
                            exObj.optString("calories", "0"),
                            exObj.optInt("duration", 60),
                            exObj.optString("instructions", "")
                    ));
                }

                list.add(new Model.Workout(
                        obj.getString("title"),
                        obj.optString("image", "ic_launcher_background"),
                        exercises
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    // Helper to find image ID by name string
    public static int getResId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }
}
