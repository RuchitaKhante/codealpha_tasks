package com.stocktrading.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * OOP Model: Represents a single buy or sell transaction.
 * Immutable record of every trade for auditing and history display.
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    public enum Type { BUY, SELL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 4)
    private Type type;             // BUY or SELL

    @Column(nullable = false)
    private Long quantity;         // number of shares traded

    @Column(nullable = false)
    private Double pricePerShare;  // price at time of trade

    @Column(nullable = false)
    private Double totalAmount;    // quantity * pricePerShare

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) timestamp = LocalDateTime.now();
        this.totalAmount = Math.round(quantity * pricePerShare * 100.0) / 100.0;
    }
}
