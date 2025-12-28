package com.dolsk.tyres.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long id;
    private Long tyreId;
    private String tyreBrand;
    private String tyreSize;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;


}
