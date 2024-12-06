package com.test.doczilla_test_task.controller;

import com.test.doczilla_test_task.model.StudentModel;
import com.test.doczilla_test_task.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @PostMapping("/students")
    public ResponseEntity<StudentModel> createStudent(@RequestParam String name,
                                                      @RequestParam String surname,
                                                      @RequestParam(required = false) String patronymic,
                                                      @RequestParam LocalDate birthDate,
                                                      @RequestParam String group) {
        try {
            studentRepository.save(name, surname, patronymic, birthDate, group);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<StudentModel> deleteStudent(@PathVariable("id") long id) {
        try {
            studentRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentModel>> getAllStudents() {
        try {
            List<StudentModel> students = new ArrayList<>(studentRepository.findAll());

            if (students.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
