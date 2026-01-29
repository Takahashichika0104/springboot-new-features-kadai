package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> findByHouse(House house) {
        return reviewRepository.findByHouseOrderByCreatedAtDesc(house);
    }

    public Review findMyReview(House house, User user) {
        return reviewRepository.findByHouseAndUser(house, user).orElse(null);
    }

    public void save(Review review) {
        reviewRepository.save(review);
    }

    public void delete(Review review) {
        reviewRepository.delete(review);
    }
}
