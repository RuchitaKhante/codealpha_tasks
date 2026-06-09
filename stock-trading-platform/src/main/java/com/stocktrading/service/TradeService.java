package com.stocktrading.service;

import com.stocktrading.model.*;
import com.stocktrading.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TradeService: Core trading engine.
 * Handles BUY and SELL operations with full validation:
 *   - Sufficient cash balance for BUY
 *   - Sufficient shares in portfolio for SELL
 *   - Weighted average cost calculation on BUY
 *   - Portfolio item cleanup when all shares are sold
 */
@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final TransactionRepository transactionRepository;

    /**
     * BUY: Deducts cash from user, adds shares to portfolio.
     * If user already owns this stock, updates weighted average buy price.
     */
    @Transactional
    public Transaction buyStock(Long userId, String symbol, Long quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        double totalCost = quantity * stock.getCurrentPrice();

        // Validation: enough cash?
        if (user.getCashBalance() < totalCost) {
            throw new RuntimeException(String.format(
                "Insufficient funds. Need $%.2f, have $%.2f",
                totalCost, user.getCashBalance()
            ));
        }

        // Validation: enough shares available?
        if (stock.getAvailableShares() < quantity) {
            throw new RuntimeException(String.format(
                "Only %d shares available for %s", stock.getAvailableShares(), symbol
            ));
        }

        // Deduct cash from user
        user.setCashBalance(Math.round((user.getCashBalance() - totalCost) * 100.0) / 100.0);
        userRepository.save(user);

        // Reduce available shares
        stock.setAvailableShares(stock.getAvailableShares() - quantity);
        stockRepository.save(stock);

        // Update portfolio: add to existing holding or create new
        PortfolioItem item = portfolioItemRepository
                .findByUserAndStock(user, stock)
                .orElse(new PortfolioItem(null, user, stock, 0L, 0.0));

        // Weighted average buy price calculation
        double totalShares = item.getQuantity() + quantity;
        double totalInvestment = (item.getQuantity() * item.getAvgBuyPrice()) + totalCost;
        item.setAvgBuyPrice(Math.round((totalInvestment / totalShares) * 100.0) / 100.0);
        item.setQuantity((long) totalShares);
        portfolioItemRepository.save(item);

        // Log the transaction
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setStock(stock);
        tx.setType(Transaction.Type.BUY);
        tx.setQuantity(quantity);
        tx.setPricePerShare(stock.getCurrentPrice());
        tx.setTotalAmount(Math.round(totalCost * 100.0) / 100.0);
        return transactionRepository.save(tx);
    }

    /**
     * SELL: Returns cash to user, removes shares from portfolio.
     * Removes the portfolio entry completely if all shares are sold.
     */
    @Transactional
    public Transaction sellStock(Long userId, String symbol, Long quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        PortfolioItem item = portfolioItemRepository
                .findByUserAndStock(user, stock)
                .orElseThrow(() -> new RuntimeException(
                    "You do not own any shares of " + symbol
                ));

        // Validation: enough shares to sell?
        if (item.getQuantity() < quantity) {
            throw new RuntimeException(String.format(
                "You only own %d shares of %s", item.getQuantity(), symbol
            ));
        }

        double totalRevenue = quantity * stock.getCurrentPrice();

        // Return cash to user
        user.setCashBalance(Math.round((user.getCashBalance() + totalRevenue) * 100.0) / 100.0);
        userRepository.save(user);

        // Restore available shares
        stock.setAvailableShares(stock.getAvailableShares() + quantity);
        stockRepository.save(stock);

        // Update or remove portfolio item
        long remainingShares = item.getQuantity() - quantity;
        if (remainingShares == 0) {
            portfolioItemRepository.delete(item);
        } else {
            item.setQuantity(remainingShares);
            portfolioItemRepository.save(item);
        }

        // Log the transaction
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setStock(stock);
        tx.setType(Transaction.Type.SELL);
        tx.setQuantity(quantity);
        tx.setPricePerShare(stock.getCurrentPrice());
        tx.setTotalAmount(Math.round(totalRevenue * 100.0) / 100.0);
        return transactionRepository.save(tx);
    }
}
