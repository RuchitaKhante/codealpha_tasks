package com.stocktrading.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * OOP Model: Represents a stock listed on the market.
 * Each stock has a symbol (e.g. AAPL), a current price,
 * and a daily change percentage.
 */
@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String symbol;         // e.g. "AAPL"

    @Column(nullable = false)
    private String companyName;    // e.g. "Apple Inc."

    @Column(nullable = false)
    private Double currentPrice;   // e.g. 178.50

    @Column(nullable = false)
    private Double previousClose;  // for % change calculation

    @Column(nullable = false)
    private String sector;         // e.g. "Technology"

    @Column(nullable = false)
    private Long availableShares;  // total shares available to trade

    // Computed: daily change %
    public Double getChangePercent() {
        if (previousClose == null || previousClose == 0) return 0.0;
        return Math.round(((currentPrice - previousClose) / previousClose) * 10000.0) / 100.0;
    }

    // Computed: absolute change
    public Double getChangeAmount() {
        return Math.round((currentPrice - previousClose) * 100.0) / 100.0;
    }
}
