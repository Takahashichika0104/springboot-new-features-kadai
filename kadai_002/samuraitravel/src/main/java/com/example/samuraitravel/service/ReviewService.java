package com.example.samuraitravel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	public List<Review> getReviewsForDisplay(Integer houseId, String email) {
		List<Review> displayReviews = new ArrayList<>();

		// ログインしている場合
		if (email != null) {
			Optional<Review> myReview = reviewRepository.findByHouseIdAndUserEmail(houseId, email);

			//ログイン済みかつ投稿済み
			if (myReview.isPresent()) {
				displayReviews.add(myReview.get());

				List<Review> otherReviews = reviewRepository.findTop5ByHouseIdAndIdNotOrderByCreatedAtDesc(houseId,
						myReview.get().getId());
				displayReviews.addAll(otherReviews);
			}
			//自分の投稿が存在しない場合
			else {
				displayReviews = reviewRepository.findTop6ByHouseIdOrderByCreatedAtDesc(houseId);
			}

		}

		//未ログイン
		else {
			displayReviews = reviewRepository.findTop6ByHouseIdOrderByCreatedAtDesc(houseId);
		}

		return displayReviews;
	}

	

	public void save(Review review) {
		reviewRepository.save(review);
	}

	public void delete(Review review) {
		reviewRepository.delete(review);
	}
}
