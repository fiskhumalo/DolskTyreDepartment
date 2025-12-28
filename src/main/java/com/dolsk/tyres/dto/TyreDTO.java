package com.dolsk.tyres.dto;




import lombok.*;

import java.math.BigDecimal;

@Data @NoArgsConstructor

public class TyreDTO {
    private Long id;
    private String brand;
    private String size;
    private BigDecimal price;
    private String description;
    private String imageUrl;

    public TyreDTO(Long id, String brand, String size, BigDecimal price, String description, String imageUrl) {
        this.id = id;
        this.brand = brand;
        this.size = size;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}



