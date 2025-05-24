package com.citymanagement.grievancesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDTO {
    private String title;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String complaintType;
    private List<MultipartFile> images;
}