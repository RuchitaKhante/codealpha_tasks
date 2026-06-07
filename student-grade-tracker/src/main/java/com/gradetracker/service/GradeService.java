package com.gradetracker.service;

import com.gradetracker.model.Student;
import com.gradetracker.repository.StudentRepository;
import java.util.List;

public class GradeService {

    private StudentRepository repository;

    public GradeService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student addStudent(String name) {
        return repository.save(name);
    }

    public boolean addGradeToStudent(int id, double grade) {
        return repository.findById(id).map(s -> {
            s.addGrade(grade);
            return true;
        }).orElse(false);
    }

    public boolean deleteStudent(int id) {
        return repository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    // Overall class average
    public double getClassAverage() {
        List<Student> all = repository.findAll();
        if (all.isEmpty()) return 0.0;
        double total = 0;
        int count = 0;
        for (Student s : all) {
            for (double g : s.getGrades()) {
                total += g;
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    // Student with highest average
    public Student getTopStudent() {
        return repository.findAll().stream()
                .max((a, b) -> Double.compare(a.getAverage(), b.getAverage()))
                .orElse(null);
    }

    // Student with lowest average
    public Student getBottomStudent() {
        return repository.findAll().stream()
                .min((a, b) -> Double.compare(a.getAverage(), b.getAverage()))
                .orElse(null);
    }
}