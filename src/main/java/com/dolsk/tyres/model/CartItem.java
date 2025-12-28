package com.dolsk.tyres.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne private Cart cart;
    @ManyToOne private Tyre tyre;
    private int quantity;
}

