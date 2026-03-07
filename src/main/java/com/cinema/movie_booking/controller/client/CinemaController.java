package com.cinema.movie_booking.controller.client;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.movie_booking.dto.cinema.CinemaResponseDTO;
import com.cinema.movie_booking.service.CinemaService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/cinemas")
@AllArgsConstructor
public class CinemaController {
    private final CinemaService cinemaService;

    @GetMapping
    public List<CinemaResponseDTO> getActiveCinemas() {
        return cinemaService.getActiveCinemas();
    }
}
