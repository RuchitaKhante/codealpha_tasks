package com.stocktrading.controller;

import com.stocktrading.model.Stock;
import com.stocktrading.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // GET /api/stocks — all stocks with updated prices
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    // GET /api/stocks/{symbol} — single stock
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) {
        return ResponseEntity.ok(stockService.getStockBySymbol(symbol));
    }

    // GET /api/stocks/sector/{sector}
    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Stock>> getBySetcor(@PathVariable String sector) {
        return ResponseEntity.ok(stockService.getStocksBySector(sector));
    }

    // POST /api/stocks/{symbol}/refresh — simulate live price tick
    @PostMapping("/{symbol}/refresh")
    public ResponseEntity<Stock> refreshPrice(@PathVariable String symbol) {
        return ResponseEntity.ok(stockService.refreshPrice(symbol));
    }
}
