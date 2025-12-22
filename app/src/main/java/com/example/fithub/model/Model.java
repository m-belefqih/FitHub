package com.example.fithub.model;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Model {

    public static class Muscle implements Serializable {
        public String id;
        public String name;
        public String imageName; // Name in JSON (e.g., "chest_img")
        public List<Exercise> exerciseList = new ArrayList<>();

        public Muscle(String id, String name, String imageName) {
            this.id = id;
            this.name = name;
            this.imageName = imageName;
        }
    }
    public static class Workout implements Serializable {
        public String title;
        public String imageName;
        public String level;
        public String description;
        public String estDuration;
        public String estCalories;
        public List<Exercise> exercises;

        // --- NEW: Full Constructor (Used by your JSON logic) ---
        public Workout(String title, String imageName, String level, String description, String estDuration, String estCalories, List<Exercise> exercises) {
            this.title = title;
            this.imageName = imageName;
            this.level = level;
            this.description = description;
            this.estDuration = estDuration;
            this.estCalories = estCalories;
            this.exercises = exercises;
        }

        // --- ADD THIS: Compatibility Constructor (Fixes DataRepository error) ---
        public Workout(String title, String imageName, List<Exercise> exercises) {
            this.title = title;
            this.imageName = imageName;
            this.exercises = exercises;
            // Default values so the app doesn't crash
            this.level = "Medium";
            this.description = "Standard workout routine.";
            this.estDuration = "30 min";
            this.estCalories = "200 kcal";
        }
    }

    public static class Exercise implements Serializable {
        public String name;
        public String imageName;
        public String difficulty;
        public String calories;
        public int duration;          // NEW: Duration in seconds
        public String instructions;   // NEW: Text instructions

        public Exercise(String name, String imageName, String difficulty, String calories, int duration, String instructions) {
            this.name = name;
            this.imageName = imageName;
            this.difficulty = difficulty;
            this.calories = calories;
            this.duration = duration;
            this.instructions = instructions;
        }
    }
    // Inside Utils.java or similar
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
