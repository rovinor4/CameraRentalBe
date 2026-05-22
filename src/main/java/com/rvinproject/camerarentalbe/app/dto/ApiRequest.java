package com.rvinproject.camerarentalbe.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ApiRequest {
    private ApiRequest() {
    }

    @Getter
    @Setter
    public static class Login {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Getter
    @Setter
    public static class AdminRequest {
        @NotBlank
        private String name;
        @NotBlank
        @Email
        private String email;
        private String password;
        @NotBlank
        private String role;
    }

    @Getter
    @Setter
    public static class CustomerRequest {
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
    }

    @Getter
    @Setter
    public static class CategoryRequest {
        @NotBlank
        private String name;
        private String description;
    }

    @Getter
    @Setter
    public static class CategoryDetailRequest {
        @NotNull
        private Long categoryId;
        @NotBlank
        private String name;
        private String description;
    }

    @Getter
    @Setter
    public static class ItemRequest {
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
    }

    @Getter
    @Setter
    public static class PaymentMethodRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String type;
        @NotBlank
        private String contentType;
        private String contentValue;
        @NotNull
        private Boolean active;
    }

    @Getter
    @Setter
    public static class RentalDetailRequest {
        @NotNull
        private Long itemId;
        @NotNull
        @Min(1)
        private Integer quantity;
    }

    @Getter
    @Setter
    public static class RentalRequest {
        @NotNull
        private Long customerId;
        @NotNull
        private LocalDate rentalDate;
        @NotNull
        private LocalDate plannedReturnDate;
        private String status;
        private String note;
        @NotEmpty
        @Valid
        private List<RentalDetailRequest> details;
    }

    @Getter
    @Setter
    public static class RentalPaymentRequest {
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
    }

    @Getter
    @Setter
    public static class ReturnRequest {
        @NotNull
        private Long rentalId;
        @NotNull
        private LocalDate returnDate;
        private String conditionNote;
        @NotNull
        private Boolean hasPenalty;
        private Long penaltyPaymentMethodId;
    }

    @Getter
    @Setter
    public static class PenaltyRequest {
        @NotNull
        private Long returnId;
        @NotBlank
        private String penaltyType;
        private String description;
        @NotNull
        @DecimalMin("0.0")
        private BigDecimal amount;
        @NotBlank
        private String status;
        private LocalDateTime paidAt;
    }

    @Getter
    @Setter
    public static class MaintenanceRequest {
        @NotNull
        private Long itemId;
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private LocalDate maintenanceDate;
        @NotNull
        @DecimalMin("0.0")
        private BigDecimal cost;
        @NotBlank
        private String status;
    }
}
