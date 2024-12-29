package com.book_store.service;

import com.book_store.entity.Feedback;
import com.book_store.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    //phân trang
    public Page<Feedback> listAllFeedback(int productId, int currentPage, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("createdAt").ascending() :
                Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(currentPage - 1, 3,sort);
        return feedbackRepository.findAllByProductId(productId, pageable);
    }

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public Feedback getFeedback(int id) {
        return feedbackRepository.findById(id).get();
    }

    public void deleteFeedback(Feedback feedback) {
        feedbackRepository.delete(feedback);
    }
}
