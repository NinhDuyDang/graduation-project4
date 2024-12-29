package com.book_store.repository;

import com.book_store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% AND p.status = 1 AND p.quantity > 0")
    public Page<Product> searchProductByName(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% AND p.category.id=?2 AND p.status = 1 AND p.quantity > 0")
    public Page<Product> searchProductByNameAndCategory(String keyword,int categoryId, Pageable pageable);

    public Page<Product> findByNameContaining(String keyword, Pageable pageable);

    @Query(value = "select * from  Products where  status = 1 order by  count desc limit 4 ", nativeQuery = true)
    public List<Product> getHotProduct();

    @Query(value = "select * from  Products where  status = 1 order by  count desc ", nativeQuery = true)
    public List<Product> getHotProductByCount();

    @Query(value = "SELECT * FROM Products where status = 1 and quantity > 0 and category_id = ? limit 4",nativeQuery = true)
    public List<Product> getProductByCategoryId(int CategoryId);
}
