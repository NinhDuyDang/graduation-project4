package com.book_store.service;

import com.book_store.entity.Staff;
import com.book_store.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    public Staff get(int id) {
        return staffRepository.findById(id).get();
    }

    public Page<Staff> listAllStaff(int currentPage, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(currentPage - 1, 6, sort);
        return staffRepository.findByNameContaining(keyword, pageable);
    }

    public void saveStaff(Staff staff) {
        staffRepository.save(staff);
    }

    public Staff getById(int id) {
        return staffRepository.getById(id);
    }

    public void delete(int id) {
        staffRepository.deleteById(id);
    }
}
