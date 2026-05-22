package com.rvinproject.camerarentalbe.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDetailRequest {
    @NotNull
    private Long categoryId;
    @NotBlank
    private String name;
    private String description;
}
