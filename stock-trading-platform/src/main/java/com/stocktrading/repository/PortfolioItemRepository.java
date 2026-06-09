package com.stocktrading.repository;

import com.stocktrading.model.PortfolioItem;
import com.stocktrading.model.User;
import com.stocktrading.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findByUser(User user);
    Optional<PortfolioItem> findByUserAndStock(User user, Stock stock);
    void deleteByUserAndStock(User user, Stock stock);
}
