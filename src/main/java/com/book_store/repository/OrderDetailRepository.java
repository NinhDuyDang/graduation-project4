package com.book_store.repository;

import com.book_store.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    public List<OrderDetail> findAllByOrderId(int orderId);

    public Set<OrderDetail> findAllByProduct_Id(int productId);
}
