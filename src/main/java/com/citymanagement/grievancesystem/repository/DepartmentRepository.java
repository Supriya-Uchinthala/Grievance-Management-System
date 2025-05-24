package com.citymanagement.grievancesystem.repository;

import com.citymanagement.grievancesystem.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
    Boolean existsByName(String name);

    // NEW METHOD: finds a department by a user's username
    Optional<Department> findByUsersUsername(String username);
}
