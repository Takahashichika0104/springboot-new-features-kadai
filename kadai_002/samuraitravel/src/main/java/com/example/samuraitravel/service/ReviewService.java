package com.example.samuraitravel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	// =====================================
	// ★ 民宿詳細ページ用
	// =====================================

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

	// =====================================
	// ★ レビュー一覧ページ用
	// =====================================
	public Page<Review> findByHouseId(Integer houseId, Pageable pageable) {
		return reviewRepository.findByHouseIdOrderByCreatedAtDesc(houseId, pageable);
	}

	// =====================================
	// ★ 保存（★ここが超重要）
	// =====================================
	@Transactional
	public void save(Review review) {

	LocalDateTime now = LocalDateTime.now();

	// 新規作成時
	if (review.getId() == null) {
	review.setCreatedAt(now);
	}

	// 新規・更新 共通
	review.setUpdatedAt(now);

	reviewRepository.save(review);
	}

	public void delete(Review review) {
	reviewRepository.delete(review);
	}
	
}