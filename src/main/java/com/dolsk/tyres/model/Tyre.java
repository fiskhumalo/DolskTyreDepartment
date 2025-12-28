package com.dolsk.tyres.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "tyres")
public class Tyre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String size;
    private BigDecimal price;

    @jakarta.persistence.Column(length = 1000)
    private String description;

    @jakarta.persistence.Column(name = "image_url")
    private String imageUrl;
}