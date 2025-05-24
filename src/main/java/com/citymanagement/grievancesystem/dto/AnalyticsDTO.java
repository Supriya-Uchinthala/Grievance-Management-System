package com.citymanagement.grievancesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    private long totalComplaints;
    private long resolvedComplaints;
    private long pendingComplaints;
    private long rejectedComplaints;

    private Map<String, Long> complaintsByType;
    private Map<String, Long> complaintsByDepartment;
    private Map<String, Long> complaintsByStatus;
    private Map<String, Long> complaintsByMonth;

    private double averageResolutionTimeInDays;
    private List<HotspotDTO> complaintHotspots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotspotDTO {
        private String area;
        private Double latitude;
        private Double longitude;
        private Long complaintCount;
    }
}