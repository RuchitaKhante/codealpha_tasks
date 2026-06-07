package com.gradetracker.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private int id;
    private String name;
    private List<Double> grades;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
        this.grades = new ArrayList<>();
    }

    // Add a grade
    public void addGrade(double grade) {
        grades.add(grade);
    }

    // Calculate average
    public double getAverage() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0;
        for (double g : grades) sum += g;
        return sum / grades.size();
    }

    // Get highest grade
    public double getHighest() {
        if (grades.isEmpty()) return 0.0;
        double max = grades.get(0);
        for (double g : grades) if (g > max) max = g;
        return max;
    }

    // Get lowest grade
    public double getLowest() {
        if (grades.isEmpty()) return 0.0;
        double min = grades.get(0);
        for (double g : grades) if (g < min) min = g;
        return min;
    }

    // Getters
    public int getId()             { return id; }
    public String getName()        { return name; }
    public List<Double> getGrades(){ return grades; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', grades=" + grades + "}";
    }
}