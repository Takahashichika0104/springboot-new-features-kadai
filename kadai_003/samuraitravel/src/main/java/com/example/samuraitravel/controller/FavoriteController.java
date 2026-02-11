package com.example.samuraitravel.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;          // ★追加
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    public FavoriteController(
            FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            HouseRepository houseRepository) {

        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.houseRepository = houseRepository;
    }

    // ======================
    // ★お気に入り一覧
    // ======================
    @GetMapping("")
    public String index(Model model, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        // ★プロ仕様（おすすめ）
        List<Favorite> favorites =
                favoriteRepository.findByUserIdWithHouse(user.getId());

        model.addAttribute("favorites", favorites);

        return "favorites/index";
    }

    // ======================
    // お気に入りトグル
    // ======================
    @PostMapping("/toggle")
    public String toggle(@RequestParam Integer houseId,
                         Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        Optional<Favorite> favorite =
                favoriteRepository.findByUserIdAndHouseId(user.getId(), houseId);

        if (favorite.isPresent()) {

            // 削除
            favoriteRepository.delete(favorite.get());

        } else {

            // 追加
            Favorite newFavorite = new Favorite();
            newFavorite.setUser(user);

            House house = houseRepository.getReferenceById(houseId);
            newFavorite.setHouse(house);

            favoriteRepository.save(newFavorite);
        }

        return "redirect:/houses/" + houseId;
    }
}
