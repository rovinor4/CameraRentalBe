package com.rvinproject.camerarentalbe.app.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RentalPaymentRequest {
    @NotNull
    private Long rentalId;
    @NotNull
    private Long paymentMethodId;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    @NotBlank
    private String status;
    private String proofImage;
    @JsonIgnore
    private MultipartFile proofImageUpload;

    @JsonIgnore
    public MultipartFile getProofImageUpload() {
        return proofImageUpload;
    }

    @JsonIgnore
    public void setProofImageUpload(MultipartFile proofImageUpload) {
        this.proofImageUpload = proofImageUpload;
    }
}
