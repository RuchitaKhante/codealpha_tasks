package com.stocktrading.controller;

import com.stocktrading.model.Transaction;
import com.stocktrading.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    /**
     * POST /api/trade/buy
     * Body: { "userId": 1, "symbol": "AAPL", "quantity": 5 }
     */
    @PostMapping("/buy")
    public ResponseEntity<?> buy(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            String symbol = body.get("symbol").toString();
            Long quantity = Long.valueOf(body.get("quantity").toString());

            Transaction tx = tradeService.buyStock(userId, symbol, quantity);
            return ResponseEntity.ok(buildResponse(tx, "BUY successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/trade/sell
     * Body: { "userId": 1, "symbol": "AAPL", "quantity": 5 }
     */
    @PostMapping("/sell")
    public ResponseEntity<?> sell(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            String symbol = body.get("symbol").toString();
            Long quantity = Long.valueOf(body.get("quantity").toString());

            Transaction tx = tradeService.sellStock(userId, symbol, quantity);
            return ResponseEntity.ok(buildResponse(tx, "SELL successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> buildResponse(Transaction tx, String message) {
        return Map.of(
            "message", message,
            "transactionId", tx.getId(),
            "type", tx.getType(),
            "symbol", tx.getStock().getSymbol(),
            "quantity", tx.getQuantity(),
            "pricePerShare", tx.getPricePerShare(),
            "totalAmount", tx.getTotalAmount(),
            "timestamp", tx.getTimestamp().toString()
        );
    }
}
