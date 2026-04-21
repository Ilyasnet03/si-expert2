package com.wafa.assurance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReaffectationRequest {
    @NotNull
    private Long expertId;
}