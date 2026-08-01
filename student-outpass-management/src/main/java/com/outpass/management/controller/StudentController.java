package com.outpass.management.controller;

import com.outpass.management.dto.OutpassDto;
import com.outpass.management.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/apply")
    public ResponseEntity<String> applyForOutpass(@RequestBody OutpassDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        studentService.applyForOutpass(username, request);
        return ResponseEntity.ok("Outpass applied successfully");
    }

    @GetMapping("/myrequests")
    public ResponseEntity<List<OutpassDto>> getMyRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(studentService.getMyRequests(username));
    }

    @DeleteMapping("/account")
    public ResponseEntity<String> deleteAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        studentService.deleteAccount(username);
        return ResponseEntity.ok("Account deleted successfully");
    }
}
