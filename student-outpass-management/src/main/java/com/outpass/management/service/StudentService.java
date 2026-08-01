package com.outpass.management.service;

import com.outpass.management.dto.OutpassDto;
import com.outpass.management.entity.OutpassRequest;
import com.outpass.management.entity.OutpassStatus;
import com.outpass.management.entity.User;
import com.outpass.management.repository.OutpassRequestRepository;
import com.outpass.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final OutpassRequestRepository outpassRequestRepository;
    private final UserRepository userRepository;

    public StudentService(OutpassRequestRepository outpassRequestRepository, UserRepository userRepository) {
        this.outpassRequestRepository = outpassRequestRepository;
        this.userRepository = userRepository;
    }

    public void applyForOutpass(String username, OutpassDto request) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        OutpassRequest outpass = new OutpassRequest();
        outpass.setReason(request.getReason());
        outpass.setOutDate(request.getOutDate());
        outpass.setReturnDate(request.getReturnDate());
        outpass.setParentPhone(request.getParentPhone());
        outpass.setStatus(OutpassStatus.PENDING);
        outpass.setStudent(student);

        outpassRequestRepository.save(outpass);
    }

    public List<OutpassDto> getMyRequests(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<OutpassRequest> requests = outpassRequestRepository.findByStudent(student);

        return requests.stream().map(this::mapToDto).collect(Collectors.toList());
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

    public void deleteAccount(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        userRepository.delete(student);
    }
}
