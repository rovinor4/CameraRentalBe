package com.rvinproject.camerarentalbe.app.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemRequest {
    @NotNull
    private Long categoryId;
    private Long categoryDetailId;
    @NotBlank
    private String name;
    private String brand;
    private String model;
    private String serialNumber;
    private String description;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal dailyPrice;
    @NotNull
    @Min(0)
    private Integer stock;
    @NotBlank
    private String status;
    private String image;
    @JsonIgnore
    private MultipartFile imageUpload;

    @JsonIgnore
    public MultipartFile getImageUpload() {
        return imageUpload;
    }

    @JsonIgnore
    public void setImageUpload(MultipartFile imageUpload) {
        this.imageUpload = imageUpload;
    }

}
