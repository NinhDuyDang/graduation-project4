package com.book_store.repository;

import com.book_store.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query(value = "SELECT * FROM Orders WHERE DATE(created_at) BETWEEN ?1 and ?2 and receiver_name like %?3%", nativeQuery = true)
    public Page<Order> findAllOrderByTime(Date startDate, Date endDate, String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM Orders WHERE DATE(created_at) >= ?1 and receiver_name like %?2%", nativeQuery = true)
    public Page<Order> findStartOrderByTime( Date startDate, String keyword,Pageable pageable);

    @Query(value = "SELECT * FROM Orders WHERE DATE(created_at) <= ?1 and receiver_name like %?2%", nativeQuery = true)
    public Page<Order> findEndOrderByTime( Date endDate,String keyword, Pageable pageable);

    public Page<Order> findByReceiverNameContaining(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM Orders where status = 2 order by id desc limit 7", nativeQuery = true)
    public List<Order> getRecentOrder();

    @Query(value = "SELECT * FROM Orders where Month(created_at) = ?1 and Year(created_at) = ?2", nativeQuery = true)
    public Page<Order> findByMonthAndYear(int month, int year, Pageable pageable);

    List<Order> findAllByCustomerId(Integer customerId);

}
