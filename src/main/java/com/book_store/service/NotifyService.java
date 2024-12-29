package com.book_store.service;

import com.book_store.entity.Notify;
import com.book_store.repository.NotifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NotifyService {
    @Autowired
    private NotifyRepository notifyRepo;

    public void save(Notify noti) {
        notifyRepo.save(noti);
    }

    public List<Notify> findAll() {
        List<Notify> noti = notifyRepo.findAll();
        Collections.reverse(noti);
        return noti;
    }
}
