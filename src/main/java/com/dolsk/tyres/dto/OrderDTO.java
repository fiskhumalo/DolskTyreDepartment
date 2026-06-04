package com.dolsk.tyres.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order request/response DTO.
 *
 * Inbound (POST /api/orders):  client sends tyreId + quantity only.
 * Outbound (GET /api/orders):  all fields populated by the service mapper.
 *
 * 'username' is intentionally read-only from the client's perspective.
 * The controller resolves it from the authenticated Principal and passes
 * it as a separate parameter to the service — it NEVER writes it into this DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    /** Null on create requests; populated in responses. */
    private Long id;

    @NotNull(message = "Tyre ID is required")
    private Long tyreId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /** Set by the service from DB — never from client input. */
    private String username;
}
