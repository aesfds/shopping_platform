package com.example.shopping_platform.home.controller;

import com.example.shopping_platform.common.ApiResponse;
import com.example.shopping_platform.home.dto.HomePageResponse;
import com.example.shopping_platform.home.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ApiResponse<HomePageResponse> getHomePage() {
        return ApiResponse.ok(homeService.getHomePage());
    }
}
