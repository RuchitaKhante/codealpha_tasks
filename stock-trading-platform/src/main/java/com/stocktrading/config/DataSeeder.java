package com.stocktrading.config;

import com.stocktrading.model.Stock;
import com.stocktrading.model.User;
import com.stocktrading.repository.StockRepository;
import com.stocktrading.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * DataSeeder: Runs at app startup to populate the H2 in-memory database
 * with 15 realistic stocks across 4 sectors, and one demo user.
 * Implements CommandLineRunner — Spring calls run() automatically.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        seedStocks();
        seedDemoUser();
        System.out.println("✅ Stock Trading Platform — Data seeded successfully!");
        System.out.println("   Demo login → username: demo  password: demo123");
        System.out.println("   H2 Console  → http://localhost:8080/h2-console");
        System.out.println("   Frontend    → http://localhost:8080");
    }

    private void seedStocks() {
        if (stockRepository.count() > 0) return;

        List<Stock> stocks = List.of(
            // Technology
            new Stock(null, "AAPL",  "Apple Inc.",             178.50, 175.20, "Technology",  50000L),
            new Stock(null, "MSFT",  "Microsoft Corporation",  415.30, 410.00, "Technology",  40000L),
            new Stock(null, "GOOGL", "Alphabet Inc.",          175.80, 172.50, "Technology",  30000L),
            new Stock(null, "NVDA",  "NVIDIA Corporation",     875.40, 860.00, "Technology",  25000L),
            new Stock(null, "META",  "Meta Platforms Inc.",    505.60, 498.00, "Technology",  35000L),

            // Finance
            new Stock(null, "JPM",   "JPMorgan Chase & Co.",   198.20, 195.50, "Finance",     45000L),
            new Stock(null, "BAC",   "Bank of America Corp.",   38.90,  38.10, "Finance",     80000L),
            new Stock(null, "GS",    "Goldman Sachs Group",    468.70, 462.00, "Finance",     20000L),

            // Healthcare
            new Stock(null, "JNJ",   "Johnson & Johnson",      152.40, 150.80, "Healthcare",  40000L),
            new Stock(null, "PFE",   "Pfizer Inc.",             28.60,  29.10, "Healthcare",  90000L),
            new Stock(null, "UNH",   "UnitedHealth Group",     510.30, 505.00, "Healthcare",  15000L),

            // Energy & Consumer
            new Stock(null, "XOM",   "Exxon Mobil Corporation", 110.20, 108.50, "Energy",    55000L),
            new Stock(null, "AMZN",  "Amazon.com Inc.",         185.60, 182.30, "Consumer",  30000L),
            new Stock(null, "TSLA",  "Tesla Inc.",              245.80, 250.00, "Consumer",  60000L),
            new Stock(null, "WMT",   "Walmart Inc.",             68.40,  67.20, "Consumer",  50000L)
        );

        stockRepository.saveAll(stocks);
        System.out.println("   Seeded " + stocks.size() + " stocks.");
    }

    private void seedDemoUser() {
        if (userRepository.existsByUsername("demo")) return;
        User demo = new User(null, "demo", "demo@stocks.com", "demo123", 10000.00);
        userRepository.save(demo);
        System.out.println("   Seeded demo user.");
    }
}
