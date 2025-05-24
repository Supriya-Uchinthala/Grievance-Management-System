package com.citymanagement.grievancesystem.service;

import com.citymanagement.grievancesystem.dto.ComplaintDTO;
import com.citymanagement.grievancesystem.dto.ComplaintStatusDTO;
import com.citymanagement.grievancesystem.exception.ResourceNotFoundException;
import com.citymanagement.grievancesystem.model.Complaint;
import com.citymanagement.grievancesystem.model.Department;
import com.citymanagement.grievancesystem.model.Status;
import com.citymanagement.grievancesystem.model.User;
import com.citymanagement.grievancesystem.repository.ComplaintRepository;
import com.citymanagement.grievancesystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;


    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private StorageService storageService;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    // Create complaint
    public Complaint createComplaint(ComplaintDTO complaintDTO, String username) {
        User user = userService.getUserByEmail(username);

        Complaint complaint = new Complaint();
        complaint.setTitle(complaintDTO.getTitle());
        complaint.setDescription(complaintDTO.getDescription());
        complaint.setAddress(complaintDTO.getAddress());
        complaint.setLatitude(complaintDTO.getLatitude());
        complaint.setLongitude(complaintDTO.getLongitude());
        complaint.setComplaintType(complaintDTO.getComplaintType());
        complaint.setSubmittedBy(user);
        complaint.setStatus(Status.SUBMITTED);
        complaint.setSubmittedAt(LocalDateTime.now());

        if (complaintDTO.getImages() != null && !complaintDTO.getImages().isEmpty()) {
            List<String> imagePaths = storageService.storeFiles(complaintDTO.getImages());
            complaint.setImagePaths(imagePaths);
        }

        // TODO: generate and set trackingId if needed

        return complaintRepository.save(complaint);
    }

    // Get complaint by ID
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));
    }

    public List<Complaint> getPendingComplaintsByDepartmentUser(String username) {
        Department department = departmentService.findDepartmentByUserUsername(username);
        return complaintRepository.findByAssignedDepartmentAndStatus(department, Status.PENDING);
    }


    // Get complaint by tracking ID
    public Complaint getComplaintByTrackingId(String trackingId) {
        return complaintRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "trackingId", trackingId));
    }

    // Get complaints by user email
    public List<Complaint> getComplaintsByUser(String username) {
        User user = userService.getUserByEmail(username);
        return complaintRepository.findBySubmittedBy(user);
    }

    public List<Complaint> getComplaintsByDepartmentUser(String username) {
        // Implement logic to get complaints for department user by username
        // For example:
        // 1. Find the department associated with the user by username
        // 2. Fetch complaints assigned to that department

        Department department = departmentService.findDepartmentByUserUsername(username);
        if (department == null) {
            return Collections.emptyList();
        }
        return complaintRepository.findByAssignedDepartment(department);
    }


    // Filtered complaints with paging
    public Page<Complaint> getFilteredComplaints(String statusStr, Long departmentId, String category,
                                                 String priority, String searchTerm, Pageable pageable) {
        // Placeholder: implement your filtering logic here
        return complaintRepository.findAll(pageable);
    }

    // Update complaint details
    public Complaint updateComplaint(Long id, ComplaintDTO complaintDTO, String username) {
        Complaint existing = getComplaintById(id);

        existing.setTitle(complaintDTO.getTitle());
        existing.setDescription(complaintDTO.getDescription());
        existing.setAddress(complaintDTO.getAddress());
        existing.setLatitude(complaintDTO.getLatitude());
        existing.setLongitude(complaintDTO.getLongitude());
        existing.setComplaintType(complaintDTO.getComplaintType());
        existing.setLastUpdatedAt(LocalDateTime.now());

        return complaintRepository.save(existing);
    }

    // Delete complaint
    public void deleteComplaint(Long id) {
        Complaint complaint = getComplaintById(id);
        complaintRepository.delete(complaint);
    }

    // Update status with comments and resolution details
    public Complaint updateComplaintStatus(ComplaintStatusDTO statusDTO, String adminEmail) {
        Complaint complaint = getComplaintById(statusDTO.getComplaintId());

        complaint.setStatus(statusDTO.getStatus());
        complaint.setAdminComments(statusDTO.getAdminComments());
        complaint.setResolutionDetails(statusDTO.getResolutionDetails());
        complaint.setLastUpdatedAt(LocalDateTime.now());

        User admin = userService.getUserByEmail(adminEmail);

        if (statusDTO.getStatus() == Status.VERIFIED) {
            complaint.setVerifiedBy(admin);
        }

        if (statusDTO.getStatus() == Status.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        return complaintRepository.save(complaint);
    }

    // Add attachment
    public Map<String, String> addAttachmentToComplaint(Long id, MultipartFile file, String username) {
        Complaint complaint = getComplaintById(id);

        String path = storageService.storeFile(file, id.toString());
        List<String> paths = complaint.getImagePaths();
        if (paths == null) {
            paths = new java.util.ArrayList<>();
        }
        paths.add(path);
        complaint.setImagePaths(paths);

        complaintRepository.save(complaint);
        return Map.of("filePath", path);
    }

    // Remove attachment
    public void removeAttachmentFromComplaint(Long id, String filename, String username) {
        Complaint complaint = getComplaintById(id);

        storageService.deleteFile(id.toString(), filename);
        complaint.getImagePaths().removeIf(p -> p.endsWith(filename));
        complaintRepository.save(complaint);
    }

    // Add comment (requires addComment method in Complaint model)
    public Complaint addCommentToComplaint(Long id, String comment, String username) {
        Complaint complaint = getComplaintById(id);
        complaint.addComment(username, comment);  // Ensure this method exists in Complaint entity
        complaint.setLastUpdatedAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }

    // Add feedback
    public Complaint addFeedbackToComplaint(Long id, Integer rating, String comments, String username) {
        Complaint complaint = getComplaintById(id);
        complaint.setRating(rating);
        complaint.setFeedbackComments(comments);
        complaint.setFeedbackBy(userService.getUserByEmail(username));
        complaint.setFeedbackAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }

    // Categories
    public List<String> getAllComplaintCategories() {
        return List.of("Road", "Water", "Electricity", "Sanitation");
    }

    // Status types
    public List<Status> getAllStatusTypes() {
        return List.of(Status.SUBMITTED, Status.IN_PROGRESS, Status.RESOLVED, Status.VERIFIED, Status.REJECTED);
    }

    // Statistics
    public Map<String, Object> getComplaintStatistics() {
        long total = complaintRepository.count();
        return Map.of("totalComplaints", total);
    }

    // Ownership check
    public boolean isComplaintOwner(Long complaintId, String username) {
        Complaint complaint = getComplaintById(complaintId);
        return complaint.getSubmittedBy().getEmail().equals(username);
    }

    // Public visibility
    public boolean isComplaintPublic(Long complaintId) {
        Complaint complaint = getComplaintById(complaintId);
        return complaint.getStatus() != Status.SUBMITTED;
    }

    // Admin helper methods
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsByStatus(Status status) {
        return complaintRepository.findByStatus(status);
    }

    public List<Complaint> getComplaintsByDateRange(LocalDateTime start, LocalDateTime end) {
        return complaintRepository.findBySubmittedAtBetween(start, end);
    }

    public List<Complaint> getPendingComplaintsByDepartmentId(Long departmentId) {
        Department department = departmentService.getDepartmentById(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("Department", "id", departmentId);
        }

        List<Status> pendingStatuses = List.of(
                Status.SUBMITTED, Status.UNDER_REVIEW, Status.ASSIGNED, Status.IN_PROGRESS
        );

        return complaintRepository.findByAssignedDepartmentAndStatusIn(department, pendingStatuses);
    }
    public List<Complaint> getComplaintsByDepartmentId(Long departmentId) {
        Department department = departmentService.getDepartmentById(departmentId);
        return complaintRepository.findByAssignedDepartment(department);
    }

    public Complaint assignComplaintToDepartment(Long complaintId, Long departmentId) {
        Complaint complaint = getComplaintById(complaintId);
        Department department = departmentService.getDepartmentById(departmentId);
        complaint.setAssignedDepartment(department);
        return complaintRepository.save(complaint);
    }


    public Map<String, Object> getDepartmentPerformanceMetrics(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        List<Complaint> complaints = complaintRepository.findByAssignedDepartment(department);

        long total = complaints.size();
        long resolved = complaints.stream().filter(c -> c.getStatus() == Status.RESOLVED).count();
        long pending = complaints.stream().filter(c -> c.getStatus() == Status.PENDING).count();
        long inProgress = complaints.stream().filter(c -> c.getStatus() == Status.IN_PROGRESS).count();


        Map<String, Object> metrics = new HashMap<>();
        metrics.put("departmentId", departmentId);
        metrics.put("departmentName", department.getName());
        metrics.put("totalComplaints", total);
        metrics.put("resolved", resolved);
        metrics.put("pending", pending);
        metrics.put("inProgress", inProgress);
        metrics.put("resolutionRate", total > 0 ? (resolved * 100.0) / total : 0.0);

        return metrics;
    }


    public Map<String, Object> getAllDepartmentsPerformanceMetrics() {
        List<Department> departments = departmentRepository.findAll();
        List<Map<String, Object>> allMetrics = new ArrayList<>();

        for (Department department : departments) {
            List<Complaint> complaints = complaintRepository.findByAssignedDepartment(department);

            long total = complaints.size();
            long resolved = complaints.stream().filter(c -> c.getStatus() == Status.RESOLVED).count();
            long pending = complaints.stream().filter(c -> c.getStatus() == Status.PENDING).count();
            long inProgress = complaints.stream().filter(c -> c.getStatus() == Status.IN_PROGRESS).count();


            Map<String, Object> deptMetrics = new HashMap<>();
            deptMetrics.put("departmentId", department.getId());
            deptMetrics.put("departmentName", department.getName());
            deptMetrics.put("totalComplaints", total);
            deptMetrics.put("resolved", resolved);
            deptMetrics.put("pending", pending);
            deptMetrics.put("inProgress", inProgress);
            deptMetrics.put("resolutionRate", total > 0 ? (resolved * 100.0) / total : 0.0);

            allMetrics.add(deptMetrics);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("departments", allMetrics);
        result.put("totalDepartments", departments.size());

        return result;
    }



}
