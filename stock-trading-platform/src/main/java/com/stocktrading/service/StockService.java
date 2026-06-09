package com.stocktrading.service;

import com.stocktrading.model.Stock;
import com.stocktrading.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

/**
 * StockService: Manages market data.
 * Simulates real-time price fluctuations using a small
 * random walk on each refresh — mimics live market behavior.
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final Random random = new Random();

    // Get all stocks (with simulated price update)
    public List<Stock> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        stocks.forEach(this::simulatePriceFluctuation);
        stockRepository.saveAll(stocks);
        return stocks;
    }

    // Get single stock by symbol
    public Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));
    }

    // Get stocks by sector
    public List<Stock> getStocksBySector(String sector) {
        return stockRepository.findBySector(sector);
    }

    /**
     * Simulates realistic price movement:
     * - Random walk: ±0.5% to ±2% change per refresh
     * - Price cannot go below $1
     */
    private void simulatePriceFluctuation(Stock stock) {
        double changePercent = (random.nextDouble() * 4.0 - 2.0) / 100.0; // -2% to +2%
        double newPrice = stock.getCurrentPrice() * (1 + changePercent);
        newPrice = Math.max(1.0, newPrice);
        newPrice = Math.round(newPrice * 100.0) / 100.0;
        stock.setCurrentPrice(newPrice);
    }

    // Refresh a specific stock's price
    public Stock refreshPrice(String symbol) {
        Stock stock = getStockBySymbol(symbol);
        simulatePriceFluctuation(stock);
        return stockRepository.save(stock);
    }
}
