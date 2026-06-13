package com.dolsk.tyres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Individual cart item in the cart response.
 * Contains denormalized tyre info so the frontend doesn't need extra calls.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long id;
    private Long tyreId;
    private String tyreBrand;
    private String tyreSize;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal lineTotal;
    private String imageUrl;
}
