package com.example.fithub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkoutModel implements Serializable {
    public String id;
    public String title;
    public String description;
    public String durationTotal;
    public String imageName;
    public List<WorkoutExercise> exerciseList = new ArrayList<>();

    public WorkoutModel(String id, String title, String description, String durationTotal, String imageName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationTotal = durationTotal;
        this.imageName = imageName;
    }

    // Inner class specifically for workout steps
    public static class WorkoutExercise implements Serializable {
        public String name;
        public String imageName;
        public int duration; // seconds
        public String instructions;

        public WorkoutExercise(String name, String imageName, int duration, String instructions) {
            this.name = name;
            this.imageName = imageName;
            this.duration = duration;
            this.instructions = instructions;
        }
    }
}
