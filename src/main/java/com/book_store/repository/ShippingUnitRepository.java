package com.book_store.repository;

import com.book_store.entity.ShippingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingUnitRepository extends JpaRepository<ShippingUnit, Integer> {
}
