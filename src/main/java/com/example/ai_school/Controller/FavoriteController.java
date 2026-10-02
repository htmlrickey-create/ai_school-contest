package com.example.ai_school.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ai_school.dto.FavoriteSchoolDto;
import com.example.ai_school.entity.Favorite;
import com.example.ai_school.entity.School;
import com.example.ai_school.repository.FavoriteRepository;
import com.example.ai_school.repository.SchoolRepository;
import com.example.ai_school.service.FavoriteService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final SchoolRepository schoolRepository;
    private final FavoriteService favoriteService;

    public FavoriteController(
            FavoriteRepository favoriteRepository,
            SchoolRepository schoolRepository,
            FavoriteService favoriteService) {

        this.favoriteRepository = favoriteRepository;
        this.schoolRepository = schoolRepository;
        this.favoriteService = favoriteService;
    }

    @PostMapping("/favorites/add")
    public String addFavorite(
            @RequestParam String schoolCode,
            HttpSession session) {

        String loginMail =
                (String) session.getAttribute("loginMail");

        if (loginMail == null) {
            return "redirect:/login";
        }

        if (!favoriteRepository.existsByLoginMailAndSchoolCode(
                loginMail,
                schoolCode)) {

            Favorite favorite = new Favorite();

            favorite.setLoginMail(loginMail);
            favorite.setSchoolCode(schoolCode);

            favoriteRepository.save(favorite);
        }

        return "redirect:/schools/" + schoolCode;
    }

    @GetMapping("/Favorite")
    public String favorites(
            Model model,
            HttpSession session) {

        String loginMail =
                (String) session.getAttribute("loginMail");

        if (loginMail == null) {
            return "redirect:/login";
        }

        List<Favorite> favorites =
                favoriteRepository.findByLoginMail(loginMail);

        List<FavoriteSchoolDto> favoriteSchoolList =
                favorites.stream()
                        .map(favorite -> {

                            School school =
                                    schoolRepository
                                            .findById(
                                                    favorite.getSchoolCode())
                                            .orElse(null);

                            return new FavoriteSchoolDto(
                                    favorite,
                                    school);
                        })
                        .toList();

        model.addAttribute(
                "favoriteSchoolList",
                favoriteSchoolList);

        return "Favorite";
    }

    @PostMapping("/favorite/delete")
    public String deleteFavorite(
            @RequestParam Long id,
            HttpSession session) {

        String loginMail =
                (String) session.getAttribute("loginMail");

        if (loginMail == null) {
            return "redirect:/login";
        }

        favoriteService.deleteFavorite(
                id,
                loginMail);

        return "redirect:/home";
    }
}