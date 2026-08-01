package com.outpass.management.repository;

import com.outpass.management.entity.OutpassRequest;
import com.outpass.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutpassRequestRepository extends JpaRepository<OutpassRequest, Long> {
    List<OutpassRequest> findByStudent(User student);
    List<OutpassRequest> findByStudent_Department(String department);
    Optional<OutpassRequest> findByQrCodeId(String qrCodeId);
}
