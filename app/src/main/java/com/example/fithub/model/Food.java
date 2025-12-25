package com.example.fithub.model;

public class Food {
    private String name;
    private long calories;
    private String image;
    private double protein;
    private double fat;
    private double carbs;
    private String servingSizeUnit;

    public Food(String name, int calories, String image, String category) {}

    public Food(String name, int calories, String image, double protein, double fat, double carbs, String servingSizeUnit) {
        this.name = name;
        this.calories = calories;
        this.image = image;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.servingSizeUnit = servingSizeUnit;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCalories() { return Math.toIntExact(calories); }
    public void setCalories(int calories) { this.calories = calories; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public String getServingSizeUnit() { return servingSizeUnit; }
    public void setServingSizeUnit(String servingSizeUnit) { this.servingSizeUnit = servingSizeUnit; }
}
