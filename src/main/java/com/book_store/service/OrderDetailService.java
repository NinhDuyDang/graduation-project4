package com.book_store.service;

import com.book_store.entity.OrderDetail;
import com.book_store.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public void saveOrderDetail(OrderDetail orderDetail){
        orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        return orderDetailRepository.findAllByOrderId(orderId);
    }

    public Set<OrderDetail> getOrderDetailsByProductId(int productId) {
        return orderDetailRepository.findAllByProduct_Id(productId);
    }
}
