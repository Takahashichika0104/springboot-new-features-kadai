package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.service.ReviewService;
import com.example.samuraitravel.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/houses/{houseId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final HouseRepository houseRepository;
	private final ReviewService reviewService;
	private final UserService userService;

	// ================================
	// ★ レビュー投稿フォーム表示
	// ================================
	@GetMapping("/new")
	public String newReview(@PathVariable Integer houseId, Model model) {

		House house = houseRepository.getReferenceById(houseId);

		model.addAttribute("house", house);
		model.addAttribute("reviewForm", new ReviewForm());

		return "reviews/create";
	}

	// （create が必要なら）
	@GetMapping("/create")
	public String create(@PathVariable Integer houseId, Model model) {

		House house = houseRepository.getReferenceById(houseId);

		model.addAttribute("house", house);
		model.addAttribute("reviewForm", new ReviewForm());

		return "reviews/create";
	}

	// ================================
	// レビュー投稿処理
	// ================================
	@PostMapping
	public String create(
			@PathVariable Integer houseId,
			@Valid ReviewForm reviewForm,
			BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {

		// バリデーションエラー
		if (bindingResult.hasErrors()) {
			House house = houseRepository.getReferenceById(houseId);
			model.addAttribute("house", house);
			return "reviews/create";
		}

		// 念のための安全対策（基本ここには来ない）
		if (userDetails == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "ログインが必要です");
			return "redirect:/login";
		}

		House house = houseRepository.getReferenceById(houseId);

		// ★ UserService は User を直接返す設計
		User user = userService.findByEmail(userDetails.getUsername());

		Review review = new Review();
		review.setHouse(house);
		review.setUser(user);
		review.setRating(reviewForm.getRating());
		review.setComment(reviewForm.getComment());

		reviewService.save(review);

		redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました");

		return "redirect:/houses/" + houseId;
	}

	//================================
	//★ レビュー一覧表示
	//================================
	@GetMapping
	public String index(@PathVariable Integer houseId, @PageableDefault(size = 10) Pageable pageable, Model model) {

		House house = houseRepository.getReferenceById(houseId);
		Page<Review> reviewPage = reviewService.findByHouseId(houseId, pageable);

		model.addAttribute("house", house);
		model.addAttribute("reviews", reviewPage.getContent());
		model.addAttribute("reviewPage", reviewPage);

		return "reviews/index";
	}

	// ================================
	// ★ レビュー編集フォーム表示
	// URL: GET /houses/{houseId}/reviews/{reviewId}/edit
	// ================================
	@GetMapping("/{reviewId}/edit")
	public String editReview(
			@PathVariable Integer houseId,
			@PathVariable Integer reviewId,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {

		Review review = reviewService.findById(reviewId);

		// 本人チェック（他人のレビュー編集防止）
		if (userDetails == null ||
				!review.getUser().getEmail().equals(userDetails.getUsername())) {
			return "redirect:/";
		}

		ReviewForm reviewForm = new ReviewForm();
		reviewForm.setRating(review.getRating());
		reviewForm.setComment(review.getComment());

		model.addAttribute("review", review);
		model.addAttribute("house", review.getHouse());
		model.addAttribute("reviewForm", reviewForm);

		return "reviews/edit";
	}

	// ================================
	// ★ レビュー更新処理
	// URL: POST /houses/{houseId}/reviews/{reviewId}/edit
	// ================================
	@PostMapping("/{reviewId}/edit")
	public String updateReview(
			@PathVariable Integer houseId,
			@PathVariable Integer reviewId,
			@Valid ReviewForm reviewForm,
			BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {

		Review review = reviewService.findById(reviewId);

		// 本人チェック
		if (userDetails == null ||
				!review.getUser().getEmail().equals(userDetails.getUsername())) {
			return "redirect:/";
		}

		// バリデーションエラー時
		if (bindingResult.hasErrors()) {
			model.addAttribute("review", review);
			model.addAttribute("house", review.getHouse());
			return "reviews/edit";
		}

		// 更新内容をセット
		review.setRating(reviewForm.getRating());
		review.setComment(reviewForm.getComment());

		// 保存（updatedAt も自動更新される）
		reviewService.save(review);

		redirectAttributes.addFlashAttribute("successMessage", "レビューを更新しました");

		return "redirect:/houses/" + houseId;
	}

	//================================
	//★ レビュー削除（POST）
	//URL: POST /houses/{houseId}/reviews/{reviewId}/delete
	//================================
	@PostMapping("/{reviewId}/delete")
	public String deleteReview(
			@PathVariable Integer houseId,
			@PathVariable Integer reviewId,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes) {

		Review review = reviewService.findById(reviewId);

		// 本人チェック
		if (userDetails == null ||
				!review.getUser().getEmail().equals(userDetails.getUsername())) {
			return "redirect:/";
		}

		reviewService.delete(review);

		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました");

		return "redirect:/houses/" + houseId;
	}

}
