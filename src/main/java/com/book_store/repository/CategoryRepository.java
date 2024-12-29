package com.book_store.repository;

import com.book_store.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    public Page<Category> findByNameContaining(String keyword, Pageable pageable);

    public Category findByName(String name);
}
