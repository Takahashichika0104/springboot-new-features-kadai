package com.example.samuraitravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.repository.HouseRepository;

@Controller
@RequestMapping("/houses/{houseId}/reviews")
public class ReviewController {

    private final HouseRepository houseRepository;

    public ReviewController(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

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
}

