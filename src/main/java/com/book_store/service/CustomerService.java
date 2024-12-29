package com.book_store.service;

import com.book_store.entity.Customer;
import com.book_store.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    //phân trang
    public Page<Customer> listAllCustomer(int currentPage, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(currentPage - 1, 6, sort);
        return customerRepository.findByNameContaining(keyword, pageable);
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getById(int id){
        return customerRepository.getById(id);
    }

    public Customer findByEmail(String email){
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }

    public void deleteCustomer(int id) {
        customerRepository.deleteById(id);
    }
}
