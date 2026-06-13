package com.dolsk.tyres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cart response DTO.
 * Total is always calculated dynamically — never stored in DB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long cartId;
    private int itemCount;
    private List<CartItemDTO> items;
    private BigDecimal total;
}
