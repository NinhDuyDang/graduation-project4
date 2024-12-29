package com.book_store.repository;

import com.book_store.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    public List<ProductImage> findAllByProductId(int id);

    public ProductImage findById(int id);
}
