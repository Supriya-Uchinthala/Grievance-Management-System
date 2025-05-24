package com.citymanagement.grievancesystem.controller;

import com.citymanagement.grievancesystem.dto.ComplaintStatusDTO;
import com.citymanagement.grievancesystem.model.Complaint;
import com.citymanagement.grievancesystem.model.Department;
import com.citymanagement.grievancesystem.service.ComplaintService;
import com.citymanagement.grievancesystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/department")
@PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ComplaintService complaintService;

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        Department newDepartment = departmentService.createDepartment(department);
        return new ResponseEntity<>(newDepartment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @RequestBody Department departmentDetails) {
        Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getDepartmentComplaints(Authentication authentication) {
        String username = authentication.getName();
        List<Complaint> complaints = complaintService.getComplaintsByDepartmentUser(username);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/complaints/pending")
    public ResponseEntity<List<Complaint>> getPendingComplaints(Authentication authentication) {
        String username = authentication.getName();
        List<Complaint> pendingComplaints = complaintService.getPendingComplaintsByDepartmentUser(username);
        return ResponseEntity.ok(pendingComplaints);
    }

    @GetMapping("/{departmentId}/complaints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Complaint>> getComplaintsByDepartment(@PathVariable Long departmentId) {
        List<Complaint> complaints = complaintService.getComplaintsByDepartmentId(departmentId);
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/complaints/{complaintId}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestBody ComplaintStatusDTO statusUpdate,
            Authentication authentication) {

        String username = authentication.getName();
        statusUpdate.setComplaintId(complaintId); // Set complaint ID into DTO

        Complaint updatedComplaint = complaintService.updateComplaintStatus(statusUpdate, username);
        return ResponseEntity.ok(updatedComplaint);
    }

    @PutMapping("/complaints/{complaintId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Complaint> assignComplaintToDepartment(
            @PathVariable Long complaintId,
            @RequestParam Long departmentId) {
        Complaint updatedComplaint = complaintService.assignComplaintToDepartment(complaintId, departmentId);
        return ResponseEntity.ok(updatedComplaint);
    }

    @GetMapping("/performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDepartmentPerformanceMetrics(
            @RequestParam(required = false) Long departmentId) {
        Map<String, Object> metrics;
        if (departmentId != null) {
            metrics = departmentService.getDepartmentPerformanceMetrics(departmentId);
        } else {
            metrics = departmentService.getAllDepartmentsPerformanceMetrics();
        }
        return ResponseEntity.ok(metrics);
    }
}
