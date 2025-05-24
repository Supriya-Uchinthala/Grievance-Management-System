// Creating the AnalyticsController.java
package com.citymanagement.grievancesystem.controller;

import com.citymanagement.grievancesystem.dto.AnalyticsDTO;
import com.citymanagement.grievancesystem.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardAnalytics() {
        Map<String, Object> dashboardData = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/complaints/summary")
    public ResponseEntity<Map<String, Object>> getComplaintsSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> summary = analyticsService.getComplaintsSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/complaints/by-status")
    public ResponseEntity<Map<String, Long>> getComplaintsByStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> complaintsByStatus = analyticsService.getComplaintCountByStatus(startDate, endDate);
        return ResponseEntity.ok(complaintsByStatus);
    }

    @GetMapping("/complaints/by-department")
    public ResponseEntity<Map<String, Long>> getComplaintsByDepartment(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> complaintsByDepartment = analyticsService.getComplaintCountByDepartment(startDate, endDate);
        return ResponseEntity.ok(complaintsByDepartment);
    }

    @GetMapping("/complaints/by-category")
    public ResponseEntity<Map<String, Long>> getComplaintsByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> complaintsByCategory = analyticsService.getComplaintCountByCategory(startDate, endDate);
        return ResponseEntity.ok(complaintsByCategory);
    }

    @GetMapping("/resolution-time")
    public ResponseEntity<Map<String, Object>> getResolutionTimeAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> resolutionTimeData = analyticsService.getResolutionTimeAnalytics(startDate, endDate);
        return ResponseEntity.ok(resolutionTimeData);
    }

    @GetMapping("/department-performance")
    public ResponseEntity<List<AnalyticsDTO>> getDepartmentPerformanceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AnalyticsDTO> performanceData = analyticsService.getDepartmentPerformanceAnalytics(startDate, endDate);
        return ResponseEntity.ok(performanceData);
    }

    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getComplaintTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") String interval) {
        Map<String, Object> trendData = analyticsService.getComplaintTrends(startDate, endDate, interval);
        return ResponseEntity.ok(trendData);
    }

    @GetMapping("/geo-distribution")
    public ResponseEntity<Map<String, Object>> getGeoDistributionAnalytics() {
        Map<String, Object> geoData = analyticsService.getGeoDistributionOfComplaints();
        return ResponseEntity.ok(geoData);
    }

    @GetMapping("/user-satisfaction")
    public ResponseEntity<Map<String, Object>> getUserSatisfactionAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> satisfactionData = analyticsService.getUserSatisfactionAnalytics(startDate, endDate);
        return ResponseEntity.ok(satisfactionData);
    }

    @GetMapping("/export")
    public ResponseEntity<AnalyticsDTO> exportAnalyticsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String format) {
        AnalyticsDTO report = analyticsService.generateAnalyticsReport(startDate, endDate, format);
        return ResponseEntity.ok(report);
    }
}
