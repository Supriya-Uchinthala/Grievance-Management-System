package com.citymanagement.grievancesystem.dto;

import com.citymanagement.grievancesystem.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintStatusDTO {
    private Long complaintId;
    private Status status;
    private Long departmentId;
    private String adminComments;
    private String resolutionDetails;
    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


}
