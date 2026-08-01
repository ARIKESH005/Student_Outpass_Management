package com.outpass.management.service;

import com.outpass.management.dto.OutpassDto;
import com.outpass.management.entity.OutpassRequest;
import com.outpass.management.entity.OutpassStatus;
import com.outpass.management.repository.OutpassRequestRepository;
import com.outpass.management.entity.User;
import com.outpass.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final OutpassRequestRepository outpassRequestRepository;
    private final UserRepository userRepository;

    public AdminService(OutpassRequestRepository outpassRequestRepository, UserRepository userRepository) {
        this.outpassRequestRepository = outpassRequestRepository;
        this.userRepository = userRepository;
    }

    public List<OutpassDto> getAllRequests(String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
                
        return outpassRequestRepository.findByStudent_Department(admin.getDepartment()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void approveRequest(Long id, String adminUsername) {
        OutpassRequest request = outpassRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outpass request not found"));
                
        User admin = userRepository.findByUsername(adminUsername).orElseThrow();
        if (!request.getStudent().getDepartment().equalsIgnoreCase(admin.getDepartment())) {
            throw new RuntimeException("Unauthorized: Cannot approve requests from other departments");
        }
        
        request.setStatus(OutpassStatus.APPROVED);
        request.setQrCodeId(java.util.UUID.randomUUID().toString());
        request.setGateScanned(false);
        outpassRequestRepository.save(request);
    }

    public void rejectRequest(Long id, String adminUsername) {
        OutpassRequest request = outpassRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outpass request not found"));
                
        User admin = userRepository.findByUsername(adminUsername).orElseThrow();
        if (!request.getStudent().getDepartment().equalsIgnoreCase(admin.getDepartment())) {
            throw new RuntimeException("Unauthorized: Cannot reject requests from other departments");
        }
        
        request.setStatus(OutpassStatus.REJECTED);
        outpassRequestRepository.save(request);
    }

    private OutpassDto mapToDto(OutpassRequest request) {
        OutpassDto dto = new OutpassDto();
        dto.setId(request.getId());
        dto.setReason(request.getReason());
        dto.setOutDate(request.getOutDate());
        dto.setReturnDate(request.getReturnDate());
        dto.setParentPhone(request.getParentPhone());
        dto.setStatus(request.getStatus());
        dto.setStudentName(request.getStudent().getUsername());
        dto.setDepartment(request.getStudent().getDepartment());
        dto.setQrCodeId(request.getQrCodeId());
        dto.setGateScanned(request.isGateScanned());
        return dto;
    }
}
