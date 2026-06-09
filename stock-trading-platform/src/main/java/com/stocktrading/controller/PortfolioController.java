package com.stocktrading.controller;

import com.stocktrading.model.PortfolioItem;
import com.stocktrading.model.Transaction;
import com.stocktrading.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    // GET /api/portfolio/{userId} — list of holdings
    @GetMapping("/{userId}")
    public ResponseEntity<List<PortfolioItem>> getPortfolio(@PathVariable Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfolio(userId));
    }

    // GET /api/portfolio/{userId}/summary — aggregated stats
    @GetMapping("/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(userId));
    }

    // GET /api/portfolio/{userId}/transactions — full trade history
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(portfolioService.getTransactionHistory(userId));
    }
}
