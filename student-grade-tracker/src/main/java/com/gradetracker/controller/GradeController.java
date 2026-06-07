package com.gradetracker.controller;

import com.gradetracker.model.Student;
import com.gradetracker.repository.StudentRepository;
import com.gradetracker.service.GradeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/")
public class GradeController {

    private static final StudentRepository repository = new StudentRepository();
    private static final GradeService service = new GradeService(repository);

    @GetMapping
    public String home(Model model) {
        List<Student> students = service.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("classAverage", service.getClassAverage());
        model.addAttribute("topStudent", service.getTopStudent());
        model.addAttribute("bottomStudent", service.getBottomStudent());
        model.addAttribute("totalStudents", students.size());
        return "index";
    }

    @PostMapping("addStudent")
    public String addStudent(@RequestParam String name, RedirectAttributes ra) {
        if (name == null || name.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Student name cannot be empty!");
            return "redirect:/";
        }
        service.addStudent(name.trim());
        ra.addFlashAttribute("success", "Student '" + name.trim() + "' added successfully!");
        return "redirect:/";
    }

    @PostMapping("addGrade")
    public String addGrade(@RequestParam int studentId,
                           @RequestParam double grade,
                           RedirectAttributes ra) {
        if (grade < 0 || grade > 100) {
            ra.addFlashAttribute("error", "Grade must be between 0 and 100!");
            return "redirect:/";
        }
        boolean ok = service.addGradeToStudent(studentId, grade);
        ra.addFlashAttribute(ok ? "success" : "error",
                ok ? "Grade added successfully!" : "Student not found!");
        return "redirect:/";
    }

    @PostMapping("deleteStudent")
    public String deleteStudent(@RequestParam int studentId, RedirectAttributes ra) {
        boolean ok = service.deleteStudent(studentId);
        ra.addFlashAttribute(ok ? "success" : "error",
                ok ? "Student deleted successfully!" : "Student not found!");
        return "redirect:/";
    }

    @GetMapping("student/{id}")
    public String studentReport(@PathVariable int id, Model model) {
        service.getAllStudents().stream()
               .filter(s -> s.getId() == id)
               .findFirst()
               .ifPresent(s -> model.addAttribute("student", s));
        return "student-report";
    }
}