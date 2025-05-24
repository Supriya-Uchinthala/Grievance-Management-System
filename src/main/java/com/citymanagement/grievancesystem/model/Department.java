package com.citymanagement.grievancesystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    @OneToMany(mappedBy = "department")
    private List<User> users;


    @OneToMany(mappedBy = "assignedDepartment")
    private Set<Complaint> complaints = new HashSet<>();
}