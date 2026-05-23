package com.rvinproject.camerarentalbe.app.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PaymentMethodRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotBlank
    private String contentType;
    private String contentValue;
    @JsonIgnore
    private MultipartFile imageUpload;
    @NotNull
    private Boolean active;

    @JsonIgnore
    public MultipartFile getImageUpload() {
        return imageUpload;
    }

    @JsonIgnore
    public void setImageUpload(MultipartFile imageUpload) {
        this.imageUpload = imageUpload;
    }

}
