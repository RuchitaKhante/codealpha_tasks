package com.stocktrading.service;

import com.stocktrading.model.PortfolioItem;
import com.stocktrading.model.Transaction;
import com.stocktrading.model.User;
import com.stocktrading.repository.PortfolioItemRepository;
import com.stocktrading.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * PortfolioService: Aggregates portfolio data for a user.
 * Calculates total invested, current value, and overall P&L.
 */
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    // Get all holdings for a user
    public List<PortfolioItem> getPortfolio(Long userId) {
        User user = userService.getUserById(userId);
        return portfolioItemRepository.findByUser(user);
    }

    // Get transaction history for a user
    public List<Transaction> getTransactionHistory(Long userId) {
        User user = userService.getUserById(userId);
        return transactionRepository.findByUserOrderByTimestampDesc(user);
    }

    // Get portfolio summary stats
    public Map<String, Object> getPortfolioSummary(Long userId) {
        User user = userService.getUserById(userId);
        List<PortfolioItem> items = portfolioItemRepository.findByUser(user);

        double totalInvested = items.stream()
                .mapToDouble(PortfolioItem::getTotalCost)
                .sum();

        double currentValue = items.stream()
                .mapToDouble(PortfolioItem::getCurrentValue)
                .sum();

        double totalPnl = currentValue - totalInvested;
        double pnlPercent = totalInvested > 0
                ? Math.round((totalPnl / totalInvested) * 10000.0) / 100.0
                : 0.0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("cashBalance", user.getCashBalance());
        summary.put("totalInvested", Math.round(totalInvested * 100.0) / 100.0);
        summary.put("currentValue", Math.round(currentValue * 100.0) / 100.0);
        summary.put("totalPnl", Math.round(totalPnl * 100.0) / 100.0);
        summary.put("pnlPercent", pnlPercent);
        summary.put("netWorth", Math.round((user.getCashBalance() + currentValue) * 100.0) / 100.0);
        summary.put("holdingsCount", items.size());
        return summary;
    }
}
