package com.citymanagement.grievancesystem.service;

import com.citymanagement.grievancesystem.dto.AnalyticsDTO;
import com.citymanagement.grievancesystem.model.Status;
import com.citymanagement.grievancesystem.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public AnalyticsDTO getAnalytics() {
        AnalyticsDTO analyticsDTO = new AnalyticsDTO();

        long totalComplaints = complaintRepository.count();
        analyticsDTO.setTotalComplaints(totalComplaints);

        long resolvedComplaints = complaintRepository.findByStatus(Status.RESOLVED).size();
        analyticsDTO.setResolvedComplaints(resolvedComplaints);

        long rejectedComplaints = complaintRepository.findByStatus(Status.REJECTED).size();
        analyticsDTO.setRejectedComplaints(rejectedComplaints);

        long pendingComplaints = totalComplaints - resolvedComplaints - rejectedComplaints;
        analyticsDTO.setPendingComplaints(pendingComplaints);

        Map<String, Long> complaintsByType = new HashMap<>();
        List<Object[]> typeData = complaintRepository.countByComplaintType();
        for (Object[] data : typeData) {
            if (data[0] != null) {
                complaintsByType.put((String) data[0], (Long) data[1]);
            }
        }
        analyticsDTO.setComplaintsByType(complaintsByType);

        Map<String, Long> complaintsByDepartment = new HashMap<>();
        List<Object[]> deptData = complaintRepository.countByDepartment();
        for (Object[] data : deptData) {
            if (data[0] != null) {
                complaintsByDepartment.put((String) data[0], (Long) data[1]);
            }
        }
        analyticsDTO.setComplaintsByDepartment(complaintsByDepartment);

        Map<String, Long> complaintsByStatus = new HashMap<>();
        List<Object[]> statusData = complaintRepository.countByStatus();
        for (Object[] data : statusData) {
            if (data[0] != null) {
                complaintsByStatus.put(((Status) data[0]).name(), (Long) data[1]);
            }
        }
        analyticsDTO.setComplaintsByStatus(complaintsByStatus);

        Map<String, Long> complaintsByMonth = new HashMap<>();
        List<Object[]> monthData = complaintRepository.countByMonth();
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (Object[] data : monthData) {
            if (data[0] != null) {
                int monthNum = ((Number) data[0]).intValue();
                if (monthNum >= 1 && monthNum <= 12) {
                    complaintsByMonth.put(monthNames[monthNum - 1], (Long) data[1]);
                }
            }
        }
        analyticsDTO.setComplaintsByMonth(complaintsByMonth);

        Double avgResolutionTime = complaintRepository.getAverageResolutionTimeInDays(Status.RESOLVED.name());
        analyticsDTO.setAverageResolutionTimeInDays(avgResolutionTime != null ? avgResolutionTime : 0.0);

        List<AnalyticsDTO.HotspotDTO> hotspots = new ArrayList<>();
        List<Object[]> hotspotData = complaintRepository.findHotspots();
        for (Object[] data : hotspotData) {
            if (data[0] != null && data[1] != null && data[2] != null) {
                AnalyticsDTO.HotspotDTO hotspot = new AnalyticsDTO.HotspotDTO(
                        (String) data[0],
                        (Double) data[1],
                        (Double) data[2],
                        (Long) data[3]
                );
                hotspots.add(hotspot);
            }
        }
        analyticsDTO.setComplaintHotspots(hotspots);

        return analyticsDTO;
    }

    // Stubbed implementations for additional endpoints
    public Map<String, Object> getDashboardAnalytics() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Dashboard data not implemented yet");
        return data;
    }

    public Map<String, Object> getComplaintsSummary(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("message", "Summary not implemented yet");
        return summary;
    }

    public Map<String, Long> getComplaintCountByStatus(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> result = new HashMap<>();
        result.put("RESOLVED", 10L);
        result.put("PENDING", 5L);
        result.put("REJECTED", 3L);
        return result;
    }

    public Map<String, Long> getComplaintCountByDepartment(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> result = new HashMap<>();
        result.put("Water", 12L);
        result.put("Electricity", 8L);
        return result;
    }

    public Map<String, Long> getComplaintCountByCategory(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> result = new HashMap<>();
        result.put("Road", 7L);
        result.put("Sanitation", 9L);
        return result;
    }

    public Map<String, Object> getResolutionTimeAnalytics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("averageResolutionTimeInDays", 4.5);
        return result;
    }

    public List<AnalyticsDTO> getDepartmentPerformanceAnalytics(LocalDate startDate, LocalDate endDate) {
        return Collections.emptyList(); // Stubbed empty list
    }

    public Map<String, Object> getComplaintTrends(LocalDate startDate, LocalDate endDate, String interval) {
        Map<String, Object> trends = new HashMap<>();
        trends.put("interval", interval);
        trends.put("message", "Trend data not implemented yet");
        return trends;
    }

    public Map<String, Object> getGeoDistributionOfComplaints() {
        Map<String, Object> geo = new HashMap<>();
        geo.put("message", "Geo distribution not implemented yet");
        return geo;
    }

    public Map<String, Object> getUserSatisfactionAnalytics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "User satisfaction analytics not implemented yet");
        return result;
    }

    public AnalyticsDTO generateAnalyticsReport(LocalDate startDate, LocalDate endDate, String format) {
        return getAnalytics(); // Return existing analytics for simplicity
    }
}
