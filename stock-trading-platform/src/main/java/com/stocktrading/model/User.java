package com.stocktrading.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * OOP Model: Represents a registered trader/user.
 * Each user has a username, email, and a cash balance
 * they can use to buy stocks.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;       // plain text for demo; use BCrypt in production

    @Column(nullable = false)
    private Double cashBalance;    // how much money the user has to invest

    // Convenience: formatted balance
    public String getFormattedBalance() {
        return String.format("$%.2f", cashBalance);
    }
}
