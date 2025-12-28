package com.dolsk.tyres.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER) // fetch immediately
    @JoinColumn(name = "user_id")       // match DB foreign key
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tyre_id")       // match DB foreign key
    private Tyre tyre;

    private Integer quantity;
    private LocalDateTime orderDate;
}
