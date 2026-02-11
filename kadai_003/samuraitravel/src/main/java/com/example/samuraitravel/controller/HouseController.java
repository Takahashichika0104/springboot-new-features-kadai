package com.example.samuraitravel.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;
	private final ReviewService reviewService;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;

	public HouseController(HouseRepository houseRepository, ReviewService reviewService,
			ReviewRepository reviewRepository, FavoriteRepository favoriteRepository) {
		this.houseRepository = houseRepository;
		this.reviewService = reviewService;
		this.reviewRepository = reviewRepository;
		this.favoriteRepository = favoriteRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<House> housePage;

		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			} else {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			}
		} else if (area != null && !area.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);

		return "houses/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		House house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);
		model.addAttribute("reservationInputForm", new ReservationInputForm());

		// レビューの情報を取って来させる
		// 表示用レビュー情報を取得
		// ユーザー名（メールアドレス）の格納用変数
		String email = null;
		//ログイン状態判定用クラスをインスタンス化
		AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
		//ログイン情報を取得
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//ログイン済みか確認する
		if (auth != null && trustResolver.isAnonymous(auth) != true) {
			email = auth.getName();
		}
		//未ログインの場合
		else {
			email = null;
		}

		//表示用レビューを取得
		List<Review> reviews = reviewService.getReviewsForDisplay(id, email);

		//model型のmodelにレビュの情報を追加
		model.addAttribute("reviews", reviews);

		// レビューを投稿しているかを確認し、htmlに渡す
		Optional<Review> myReview = reviewRepository.findByHouseIdAndUserEmail(id, email);
		boolean alreadyReviewed = myReview.isPresent();

		model.addAttribute("alreadyReviewed", alreadyReviewed);

		// ======================
		// ★ お気に入り判定（追加）
		// ======================
		boolean isFavorite = false;

		if (email != null) {
			Optional<Favorite> favorite = favoriteRepository.findByHouseIdAndUserEmail(id, email);

			isFavorite = favorite.isPresent();
		}

		model.addAttribute("isFavorite", isFavorite);

		return "houses/show";
	}
}