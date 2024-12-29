package com.book_store.service;

import com.book_store.entity.Order;
import com.book_store.entity.OrderDetail;
import com.book_store.repository.OrderDetailRepository;
import com.book_store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Page<Order> sortByTime(int currentPage, String keyword, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(currentPage - 1, 6);
        if (startDate != null && endDate != null) {
            return orderRepository.findAllOrderByTime(startDate, endDate, keyword, pageable);
        }
        if (startDate == null && endDate != null) {
            return orderRepository.findEndOrderByTime(endDate, keyword, pageable);
        }
        if (endDate == null && startDate != null) {
            return orderRepository.findStartOrderByTime(startDate, keyword, pageable);
        }
        return orderRepository.findByReceiverNameContaining(keyword, pageable);
    }

    public List<OrderDetail> getOrderDetailByID(int orderDetailId) {
        return orderDetailRepository.findAllByOrderId(orderDetailId);
    }

    public Order getById(int id) {
        return orderRepository.getById(id);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getLastestOrders() {
        return orderRepository.getRecentOrder();
    }

    public Integer getDataRevenueByMonthOfYear(int month, int year) {
        List<Order> orderList = orderRepository.findAll();
        Integer revenue = 0;
        for (Order order : orderList) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(order.getCreatedAt());
            if ((calendar.get(Calendar.MONTH) + 1) == month && calendar.get(Calendar.YEAR) == year && order.getStatus() == 2) {
                revenue += order.getPrice().intValue();
            }
        }
        return revenue;
    }

    public Page<Order> listOrderByMonthAndYear(int currentPage, int month, int year) {
        Pageable pageable = PageRequest.of(currentPage - 1, 6);
        return orderRepository.findByMonthAndYear(month, year, pageable);
    }

    public List<Order> getAllUserOrder(int customerId) {
        return orderRepository.findAllByCustomerId(customerId);
    }

    public Order findOrderbyOrderId(int orderId) {
        return orderRepository.getById(orderId);
    }
}
