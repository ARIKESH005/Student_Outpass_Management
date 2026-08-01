package com.outpass.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "outpass_requests")
public class OutpassRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reason;

    @Column(name = "out_date", nullable = false)
    private LocalDate outDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "parent_phone", nullable = false)
    private String parentPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutpassStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "qr_code_id", unique = true)
    private String qrCodeId;

    @Column(name = "gate_scanned", nullable = false)
    private boolean gateScanned = false;

    public OutpassRequest() {}

    public OutpassRequest(Long id, String reason, LocalDate outDate, LocalDate returnDate, String parentPhone, OutpassStatus status, User student, String qrCodeId, boolean gateScanned) {
        this.id = id;
        this.reason = reason;
        this.outDate = outDate;
        this.returnDate = returnDate;
        this.parentPhone = parentPhone;
        this.status = status;
        this.student = student;
        this.qrCodeId = qrCodeId;
        this.gateScanned = gateScanned;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDate getOutDate() { return outDate; }
    public void setOutDate(LocalDate outDate) { this.outDate = outDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
    public OutpassStatus getStatus() { return status; }
    public void setStatus(OutpassStatus status) { this.status = status; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public String getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(String qrCodeId) { this.qrCodeId = qrCodeId; }
    public boolean isGateScanned() { return gateScanned; }
    public void setGateScanned(boolean gateScanned) { this.gateScanned = gateScanned; }
}
