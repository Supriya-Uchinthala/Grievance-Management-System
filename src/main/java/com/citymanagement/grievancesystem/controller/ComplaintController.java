package com.citymanagement.grievancesystem.controller;

import com.citymanagement.grievancesystem.dto.ComplaintDTO;
import com.citymanagement.grievancesystem.dto.ComplaintStatusDTO;
import com.citymanagement.grievancesystem.model.Complaint;
import com.citymanagement.grievancesystem.model.Status;
import com.citymanagement.grievancesystem.service.ComplaintService;
import com.citymanagement.grievancesystem.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private StorageService storageService;

    // Other endpoints remain unchanged...

    // Status updates
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody ComplaintStatusDTO statusUpdate,
            Authentication authentication) {
        String username = authentication.getName();

        // Ensure the complaintId in DTO matches path variable id
        statusUpdate.setComplaintId(id);

        Complaint updatedComplaint = complaintService.updateComplaintStatus(statusUpdate, username);
        return ResponseEntity.ok(updatedComplaint);
    }

    // Other endpoints remain unchanged...
}
