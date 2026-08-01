package com.outpass.management.controller;

import com.outpass.management.dto.OutpassDto;
import com.outpass.management.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<OutpassDto>> getAllRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = authentication.getName();
        return ResponseEntity.ok(adminService.getAllRequests(adminUsername));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = authentication.getName();
        adminService.approveRequest(id, adminUsername);
        return ResponseEntity.ok("Request approved successfully");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = authentication.getName();
        adminService.rejectRequest(id, adminUsername);
        return ResponseEntity.ok("Request rejected successfully");
    }
}
