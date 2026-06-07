package com.gradetracker.repository;

import com.gradetracker.model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository {

    private List<Student> students = new ArrayList<>();
    private int idCounter = 1;

    public Student save(String name) {
        Student s = new Student(idCounter++, name);
        students.add(s);
        return s;
    }

    public List<Student> findAll() {
        return students;
    }

    public Optional<Student> findById(int id) {
        return students.stream()
                       .filter(s -> s.getId() == id)
                       .findFirst();
    }

    public boolean deleteById(int id) {
        return students.removeIf(s -> s.getId() == id);
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }
}