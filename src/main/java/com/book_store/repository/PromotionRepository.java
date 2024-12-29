package com.book_store.repository;

import com.book_store.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    public Page<Promotion> findByNameContaining(String keyword, Pageable pageable);

    public Promotion findByCode(String Code);

    public Promotion findByName(String name);
}
