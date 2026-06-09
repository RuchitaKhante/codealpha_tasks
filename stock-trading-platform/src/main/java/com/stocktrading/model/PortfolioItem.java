package com.stocktrading.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * OOP Model: Represents a single stock holding in a user's portfolio.
 * Tracks how many shares the user owns and their average buy price
 * to calculate profit/loss.
 */
@Entity
@Table(name = "portfolio_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private Long quantity;         // number of shares owned

    @Column(nullable = false)
    private Double avgBuyPrice;    // weighted average purchase price

    // Computed: current market value of this holding
    public Double getCurrentValue() {
        return Math.round(quantity * stock.getCurrentPrice() * 100.0) / 100.0;
    }

    // Computed: total amount invested
    public Double getTotalCost() {
        return Math.round(quantity * avgBuyPrice * 100.0) / 100.0;
    }

    // Computed: profit or loss
    public Double getProfitLoss() {
        return Math.round((getCurrentValue() - getTotalCost()) * 100.0) / 100.0;
    }

    // Computed: profit/loss %
    public Double getProfitLossPercent() {
        if (getTotalCost() == 0) return 0.0;
        return Math.round((getProfitLoss() / getTotalCost()) * 10000.0) / 100.0;
    }
}
