package com.outpass.management.controller;

import com.outpass.management.entity.OutpassRequest;
import com.outpass.management.entity.OutpassStatus;
import com.outpass.management.repository.OutpassRequestRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class GateScannerController {

    private final OutpassRequestRepository outpassRequestRepository;

    public GateScannerController(OutpassRequestRepository outpassRequestRepository) {
        this.outpassRequestRepository = outpassRequestRepository;
    }

    @GetMapping(value = "/scan/{qrCodeId}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> scanQrCode(@PathVariable String qrCodeId) {
        Optional<OutpassRequest> requestOpt = outpassRequestRepository.findByQrCodeId(qrCodeId);

        if (requestOpt.isEmpty()) {
            return ResponseEntity.ok(buildHtmlResponse(false, "Invalid QR Code", "This QR code does not exist in our system.", "#ef4444", "fa-circle-xmark"));
        }

        OutpassRequest request = requestOpt.get();

        if (request.getStatus() != OutpassStatus.APPROVED) {
            return ResponseEntity.ok(buildHtmlResponse(false, "Outpass Not Approved", "This outpass is currently " + request.getStatus() + ".", "#ef4444", "fa-circle-xmark"));
        }

        if (request.isGateScanned()) {
            return ResponseEntity.ok(buildHtmlResponse(false, "Already Used", "This QR code has already been scanned at the gate.", "#f59e0b", "fa-triangle-exclamation"));
        }

        // Mark as scanned
        request.setGateScanned(true);
        outpassRequestRepository.save(request);

        String studentDetails = "Student: " + request.getStudent().getUsername() + "<br>Dept: " + request.getStudent().getDepartment();
        return ResponseEntity.ok(buildHtmlResponse(true, "Access Granted", "Valid Outpass. Student may leave.<br><br>" + studentDetails, "#10b981", "fa-circle-check"));
    }

    private String buildHtmlResponse(boolean success, String title, String message, String color, String icon) {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<link href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\" rel=\"stylesheet\">" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background-color: #f3f4f6; }" +
                ".card { background: white; padding: 3rem; border-radius: 1rem; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); text-align: center; max-width: 400px; width: 90%; }" +
                "i { font-size: 5rem; color: " + color + "; margin-bottom: 1.5rem; }" +
                "h1 { color: #1f2937; margin-bottom: 0.5rem; font-size: 1.875rem; }" +
                "p { color: #4b5563; font-size: 1.125rem; line-height: 1.5; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"card\">" +
                "<i class=\"fa-solid " + icon + "\"></i>" +
                "<h1>" + title + "</h1>" +
                "<p>" + message + "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
