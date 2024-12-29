package com.book_store.repository;

import com.book_store.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    public List<Cart> findByCustomer_Id(int customerId) ;

    public List<Cart> findByCustomer_IdAndStatus(int customerId, int status) ;

}
