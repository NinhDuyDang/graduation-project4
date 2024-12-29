package com.book_store.repository;

import com.book_store.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    public Page<Customer> findByNameContaining(String keyword, Pageable pageable);

    public Customer findByEmail(String email);
}
