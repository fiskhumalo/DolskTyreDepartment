package com.dolsk.tyres.dto;

import jakarta.validation.constraints.Min;
import lombok.*;
import jakarta.validation.constraints.NotNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class OrderDTO {
    private Long id;
    @NotNull private Long tyreId;
    @NotNull @Min(1) private Integer quantity;
    private String username;
}