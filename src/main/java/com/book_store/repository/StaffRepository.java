package com.book_store.repository;

import com.book_store.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    public Page<Staff> findByNameContaining(String keyword, Pageable pageable);
    public Staff findByEmail(String email);
}
