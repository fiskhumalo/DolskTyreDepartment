package com.dolsk.tyres.dto;




import lombok.*;
import jakarta.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank private String username;
    @NotBlank private String password;
}
