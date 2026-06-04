package com.dolsk.tyres.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Tyre request/response DTO.
 * Does NOT import or reference the Tyre entity — mapping is the service's responsibility.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TyreDTO {

    /** Null on create requests; populated in all responses. */
    private Long id;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Size is required")
    private String size;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    private String description;

    private String imageUrl;
}
