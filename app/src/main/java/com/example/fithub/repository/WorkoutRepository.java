package com.example.fithub.repository;

import android.content.Context;

import com.example.fithub.model.WorkoutModel;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WorkoutRepository {

    public static List<WorkoutModel> getWorkouts(Context context) {
        List<WorkoutModel> list = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("workouts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                WorkoutModel workout = new WorkoutModel(
                        obj.getString("id"),
                        obj.getString("title"),
                        obj.getString("description"),
                        obj.getString("duration_total"),
                        obj.optString("image", "ic_launcher_background")
                );

                JSONArray exercises = obj.getJSONArray("exercises");
                for (int j = 0; j < exercises.length(); j++) {
                    JSONObject ex = exercises.getJSONObject(j);
                    workout.exerciseList.add(new WorkoutModel.WorkoutExercise(
                            ex.getString("name"),
                            ex.optString("image", "ic_launcher_foreground"),
                            ex.getInt("duration"),
                            ex.optString("instructions", "")
                    ));
                }
                list.add(workout);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    // Add this method to WorkoutRepository class
    public static WorkoutModel getWorkoutById(Context context, String id) {
        List<WorkoutModel> allWorkouts = getWorkouts(context);
        for (WorkoutModel w : allWorkouts) {
            if (w.id.equals(id)) {
                return w;
            }
        }
        return null; // Not found
    }
}
