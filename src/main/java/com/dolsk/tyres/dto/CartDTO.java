package com.dolsk.tyres.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CartDTO {

    private Long cartId;
    private List<CartItemDTO> items;
    private BigDecimal total;


}
