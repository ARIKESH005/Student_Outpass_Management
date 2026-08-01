package com.outpass.management.dto;

import com.outpass.management.entity.OutpassStatus;
import java.time.LocalDate;

public class OutpassDto {
    private Long id;
    private String reason;
    private LocalDate outDate;
    private LocalDate returnDate;
    private String parentPhone;
    private OutpassStatus status;
    private String studentName;
    private String department;
    private String qrCodeId;
    private boolean gateScanned;

    public OutpassDto() {}

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
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(String qrCodeId) { this.qrCodeId = qrCodeId; }
    public boolean isGateScanned() { return gateScanned; }
    public void setGateScanned(boolean gateScanned) { this.gateScanned = gateScanned; }
}
