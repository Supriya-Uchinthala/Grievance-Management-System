package com.citymanagement.grievancesystem.service;

import com.citymanagement.grievancesystem.model.Complaint;
import com.citymanagement.grievancesystem.model.Department;
import com.citymanagement.grievancesystem.repository.ComplaintRepository;
import com.citymanagement.grievancesystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = getDepartmentById(id);
        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }

    // ✅ For one department's metrics
    public Map<String, Object> getDepartmentPerformanceMetrics(Long departmentId) {
        Department department = getDepartmentById(departmentId);
        List<Complaint> complaints = complaintRepository.findByAssignedDepartment_Id(departmentId);

        long total = complaints.size();
        long resolved = complaints.stream().filter(c -> c.getStatus().name().equals("RESOLVED")).count();
        long pending = total - resolved;

        Map<String, Object> result = new HashMap<>();
        result.put("departmentId", departmentId);
        result.put("departmentName", department.getName());
        result.put("totalComplaints", total);
        result.put("resolvedComplaints", resolved);
        result.put("pendingComplaints", pending);

        return result;
    }

    // ✅ For all departments' metrics
    public Map<String, Object> getAllDepartmentsPerformanceMetrics() {
        Map<String, Object> allMetrics = new LinkedHashMap<>();
        List<Department> departments = departmentRepository.findAll();

        for (Department dept : departments) {
            Map<String, Object> deptMetrics = getDepartmentPerformanceMetrics(dept.getId());
            allMetrics.put(dept.getName(), deptMetrics);
        }

        return allMetrics;
    }

    // In DepartmentService.java
    public Department findDepartmentByUserUsername(String username) {
        return departmentRepository.findByUsersUsername(username)
                .orElseThrow(() -> new RuntimeException("Department not found for user: " + username));
    }

}
