package com.rvinproject.camerarentalbe.app.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CustomerRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    @NotBlank
    private String address;
    @NotBlank
    private String identityType;
    @NotBlank
    private String identityNumber;
    private String identityImage;
    @JsonIgnore
    private MultipartFile identityImageUpload;

    @JsonIgnore
    public MultipartFile getIdentityImageUpload() {
        return identityImageUpload;
    }

    @JsonIgnore
    public void setIdentityImageUpload(MultipartFile identityImageUpload) {
        this.identityImageUpload = identityImageUpload;
    }
}
