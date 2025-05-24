package com.citymanagement.grievancesystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    private String priority;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String address;

    private Double latitude;

    private Double longitude;

    @ElementCollection
    @CollectionTable(name = "complaint_images", joinColumns = @JoinColumn(name = "complaint_id"))
    @Column(name = "image_path")
    private List<String> imagePaths = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.SUBMITTED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id", nullable = false)
    private User submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_department_id")
    private Department assignedDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id")
    private User verifiedBy;

    @Column(nullable = false)
    private String complaintType;

    @Column(length = 3000)
    private String resolutionDetails;

    @Column(length = 3000)
    private String adminComments;

    @Column(unique = true, nullable = false)
    private String trackingId;

    // Optional: Feedback Fields
    private Integer rating;

    @Column(length = 2000)
    private String feedbackComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_by_id")
    private User feedbackBy;

    private LocalDateTime feedbackAt;

    // Optional: Comments Log (if you want to support multiple comments)
    @ElementCollection
    @CollectionTable(name = "complaint_comments", joinColumns = @JoinColumn(name = "complaint_id"))
    @Column(name = "comment", length = 1000)
    private List<String> commentLog = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.submittedAt = now;
        this.createdAt = now;
        this.lastUpdatedAt = now;
        if (this.trackingId == null) {
            this.trackingId = generateTrackingId();
        }
        if (this.status == null) {
            this.status = Status.SUBMITTED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
        if (this.status == Status.RESOLVED && this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    private String generateTrackingId() {
        // Generate a unique tracking ID format: GMS-YEAR-RANDOMNUMBER
        long timestamp = System.currentTimeMillis();
        return "GMS-" + LocalDateTime.now().getYear() + "-" + (timestamp % 100000);
    }

    // ✅ Add comment by appending to commentLog list
    public void addComment(String username, String comment) {
        if (this.commentLog == null) {
            this.commentLog = new ArrayList<>();
        }
        String timestampedComment = "[" + LocalDateTime.now() + "] [" + username + "] " + comment;
        this.commentLog.add(timestampedComment);
    }

    // Helper method to check if complaint is resolved
    public boolean isResolved() {
        return this.status == Status.RESOLVED;
    }

    // Helper method to check if complaint is assigned
    public boolean isAssigned() {
        return this.assignedDepartment != null;
    }
}