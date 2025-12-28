package com.dolsk.tyres.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rating;
    @Column(length = 1000)
    private String comment;
    @ManyToOne private User user;
    @ManyToOne private Tyre tyre;
    private LocalDateTime createdAt = LocalDateTime.now();
}
