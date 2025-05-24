package com.citymanagement.grievancesystem.repository;

import com.citymanagement.grievancesystem.model.Complaint;
import com.citymanagement.grievancesystem.model.Department;
import com.citymanagement.grievancesystem.model.Status;
import com.citymanagement.grievancesystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Basic finder methods with index hints
    List<Complaint> findByAssignedDepartment_Id(Long departmentId);
    List<Complaint> findBySubmittedBy(User user);
    List<Complaint> findByAssignedDepartmentAndStatusIn(Department department, List<Status> statuses);
    List<Complaint> findByAssignedDepartment(Department department);
    List<Complaint> findByStatus(Status status);
    Optional<Complaint> findByTrackingId(String trackingId);
    List<Complaint> findByStatusAndAssignedDepartment(Status status, Department department);
    List<Complaint> findBySubmittedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Complaint> findByAssignedDepartmentAndStatus(Department department, Status status);
    List<Complaint> findByComplaintType(String complaintType);

    // Paginated versions for better performance
    Page<Complaint> findByAssignedDepartment(Department department, Pageable pageable);
    Page<Complaint> findByStatus(Status status, Pageable pageable);
    Page<Complaint> findBySubmittedBy(User user, Pageable pageable);

    // Date range queries with PostgreSQL date functions
    List<Complaint> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Statistical queries optimized for PostgreSQL
    @Query("SELECT c.complaintType, COUNT(c) FROM Complaint c GROUP BY c.complaintType ORDER BY COUNT(c) DESC")
    List<Object[]> countByComplaintType();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status ORDER BY COUNT(c) DESC")
    List<Object[]> countByStatus();

    @Query("SELECT d.name, COUNT(c) FROM Complaint c JOIN c.assignedDepartment d WHERE c.assignedDepartment IS NOT NULL GROUP BY d.name ORDER BY COUNT(c) DESC")
    List<Object[]> countByDepartment();

    // PostgreSQL-specific date extraction with better performance
    @Query(value = "SELECT EXTRACT(MONTH FROM submitted_at) as month, COUNT(*) as count " +
            "FROM complaints " +
            "WHERE submitted_at >= CURRENT_DATE - INTERVAL '12 months' " +
            "GROUP BY EXTRACT(MONTH FROM submitted_at) " +
            "ORDER BY month", nativeQuery = true)
    List<Object[]> countByMonth();

    @Query(value = "SELECT EXTRACT(YEAR FROM submitted_at) as year, " +
            "EXTRACT(MONTH FROM submitted_at) as month, COUNT(*) as count " +
            "FROM complaints " +
            "GROUP BY EXTRACT(YEAR FROM submitted_at), EXTRACT(MONTH FROM submitted_at) " +
            "ORDER BY year DESC, month DESC " +
            "LIMIT 24", nativeQuery = true)
    List<Object[]> countByYearAndMonth();

    // Enhanced hotspot detection with geospatial clustering
    @Query(value = "SELECT address, latitude, longitude, COUNT(*) as complaint_count, " +
            "ST_ClusterKMeans(ST_MakePoint(longitude, latitude), 10) OVER() as cluster_id " +
            "FROM complaints " +
            "WHERE latitude IS NOT NULL AND longitude IS NOT NULL " +
            "GROUP BY address, latitude, longitude " +
            "HAVING COUNT(*) > 1 " +
            "ORDER BY complaint_count DESC", nativeQuery = true)
    List<Object[]> findHotspotsWithClustering();

    // Standard hotspot query (keeping for compatibility)
    @Query("SELECT c.address, c.latitude, c.longitude, COUNT(c) as count FROM Complaint c WHERE c.latitude IS NOT NULL AND c.longitude IS NOT NULL GROUP BY c.address, c.latitude, c.longitude HAVING COUNT(c) > 1 ORDER BY COUNT(c) DESC")
    List<Object[]> findHotspots();

    // PostgreSQL-optimized resolution time calculation
    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (resolved_at - created_at))/86400) " +
            "FROM complaints " +
            "WHERE status = CAST(:status AS varchar) AND resolved_at IS NOT NULL",
            nativeQuery = true)
    Double getAverageResolutionTimeInDays(@Param("status") String status);

    // Resolution time with percentiles (PostgreSQL specific)
    @Query(value = "SELECT " +
            "AVG(EXTRACT(EPOCH FROM (resolved_at - created_at))/86400) as avg_days, " +
            "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY EXTRACT(EPOCH FROM (resolved_at - created_at))/86400) as median_days, " +
            "PERCENTILE_CONT(0.9) WITHIN GROUP (ORDER BY EXTRACT(EPOCH FROM (resolved_at - created_at))/86400) as p90_days " +
            "FROM complaints " +
            "WHERE status = CAST(:status AS varchar) AND resolved_at IS NOT NULL",
            nativeQuery = true)
    Object[] getResolutionTimeStatistics(@Param("status") String status);

    // Efficient counting with proper indexing
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.assignedDepartment = :department AND c.status IN :statuses")
    Long countByDepartmentAndStatusIn(@Param("department") Department department, @Param("statuses") List<Status> statuses);

    // Overdue complaints with configurable SLA
    @Query("SELECT c FROM Complaint c WHERE c.status = :status AND c.createdAt < :cutoffDate ORDER BY c.createdAt ASC")
    List<Complaint> findOverdueComplaints(@Param("status") Status status, @Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT c FROM Complaint c WHERE c.assignedDepartment IS NULL AND c.status = :status ORDER BY c.createdAt ASC")
    List<Complaint> findUnassignedComplaints(@Param("status") Status status);

    // Enhanced full-text search using PostgreSQL's text search capabilities
    @Query(value = "SELECT * FROM complaints " +
            "WHERE to_tsvector('english', title || ' ' || description || ' ' || COALESCE(address, '')) " +
            "@@ plainto_tsquery('english', :keyword) " +
            "ORDER BY ts_rank(to_tsvector('english', title || ' ' || description || ' ' || COALESCE(address, '')), " +
            "plainto_tsquery('english', :keyword)) DESC",
            nativeQuery = true)
    List<Complaint> searchComplaintsFullText(@Param("keyword") String keyword);

    // Fallback search for simple keyword matching
    @Query("SELECT c FROM Complaint c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Complaint> searchComplaints(@Param("keyword") String keyword);

    // Complex filtering with better performance
    @Query("SELECT c FROM Complaint c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:departmentId IS NULL OR c.assignedDepartment.id = :departmentId) AND " +
            "(:complaintType IS NULL OR c.complaintType = :complaintType) AND " +
            "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR c.createdAt <= :endDate) " +
            "ORDER BY c.createdAt DESC")
    Page<Complaint> findComplaintsWithFilters(
            @Param("status") Status status,
            @Param("departmentId") Long departmentId,
            @Param("complaintType") String complaintType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Priority-based queries
    @Query("SELECT c FROM Complaint c WHERE c.priority = :priority AND c.status IN :statuses ORDER BY c.createdAt ASC")
    List<Complaint> findByPriorityAndStatusIn(@Param("priority") String priority, @Param("statuses") List<Status> statuses);

    // Geographic queries using PostGIS functions (if PostGIS is enabled)
    @Query(value = "SELECT * FROM complaints " +
            "WHERE ST_DWithin(ST_MakePoint(longitude, latitude)::geography, " +
            "ST_MakePoint(:longitude, :latitude)::geography, :radiusMeters) " +
            "AND latitude IS NOT NULL AND longitude IS NOT NULL " +
            "ORDER BY ST_Distance(ST_MakePoint(longitude, latitude)::geography, " +
            "ST_MakePoint(:longitude, :latitude)::geography)",
            nativeQuery = true)
    List<Complaint> findComplaintsNearLocation(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters);

    // Time-based analytics
    @Query(value = "SELECT " +
            "DATE_TRUNC('hour', created_at) as hour_bucket, " +
            "COUNT(*) as complaint_count " +
            "FROM complaints " +
            "WHERE created_at >= :startDate AND created_at <= :endDate " +
            "GROUP BY DATE_TRUNC('hour', created_at) " +
            "ORDER BY hour_bucket",
            nativeQuery = true)
    List<Object[]> getComplaintsByHour(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Department workload analysis
    @Query(value = "SELECT d.name, " +
            "COUNT(CASE WHEN c.status = 'OPEN' THEN 1 END) as open_count, " +
            "COUNT(CASE WHEN c.status = 'IN_PROGRESS' THEN 1 END) as in_progress_count, " +
            "COUNT(CASE WHEN c.status = 'RESOLVED' THEN 1 END) as resolved_count, " +
            "AVG(CASE WHEN c.resolved_at IS NOT NULL THEN " +
            "EXTRACT(EPOCH FROM (c.resolved_at - c.created_at))/86400 END) as avg_resolution_days " +
            "FROM complaints c " +
            "RIGHT JOIN departments d ON c.assigned_department_id = d.id " +
            "GROUP BY d.id, d.name " +
            "ORDER BY d.name",
            nativeQuery = true)
    List<Object[]> getDepartmentWorkloadAnalysis();

    // Count queries for pagination
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.assignedDepartment = :department")
    Long countByAssignedDepartment(@Param("department") Department department);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status")
    Long countByStatus(@Param("status") Status status);

    // Bulk update operations
    @Modifying
    @Query("UPDATE Complaint c SET c.status = :newStatus WHERE c.status = :oldStatus AND c.assignedDepartment = :department")
    int bulkUpdateStatus(@Param("oldStatus") Status oldStatus, @Param("newStatus") Status newStatus, @Param("department") Department department);

    // Recent complaints with limit
    @Query("SELECT c FROM Complaint c ORDER BY c.createdAt DESC")
    List<Complaint> findRecentComplaints(Pageable pageable);
}