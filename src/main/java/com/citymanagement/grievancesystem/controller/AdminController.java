package com.citymanagement.grievancesystem.controller;

import com.citymanagement.grievancesystem.dto.ComplaintStatusDTO;
import com.citymanagement.grievancesystem.model.Complaint;
import com.citymanagement.grievancesystem.model.Status;
import com.citymanagement.grievancesystem.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ComplaintService complaintService;

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/complaints/{status}")
    public ResponseEntity<List<Complaint>> getComplaintsByStatus(@PathVariable Status status) {
        List<Complaint> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/complaints/update-status")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @RequestBody ComplaintStatusDTO statusDTO,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        Complaint updatedComplaint = complaintService.updateComplaintStatus(statusDTO, adminEmail);
        return ResponseEntity.ok(updatedComplaint);
    }

    @DeleteMapping("/complaints/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/complaints/date-range")
    public ResponseEntity<List<Complaint>> getComplaintsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<Complaint> complaints = complaintService.getComplaintsByDateRange(start, end);
        return ResponseEntity.ok(complaints);
    }
}