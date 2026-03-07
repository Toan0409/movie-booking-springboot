package com.cinema.movie_booking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.movie_booking.dto.cinema.CinemaRequestDTO;
import com.cinema.movie_booking.dto.cinema.CinemaResponseDTO;
import com.cinema.movie_booking.service.CinemaService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/admin/cinemas")
@AllArgsConstructor
public class CinemaAdminController {
    private final CinemaService cinemaService;

    @GetMapping
    public List<CinemaResponseDTO> getAll() {
        return cinemaService.getAllCinemas();
    }

    @GetMapping("/{id}")
    public CinemaResponseDTO getById(@PathVariable Long id) {
        return cinemaService.getCinemaById(id);
    }

    @PostMapping
    public CinemaResponseDTO create(@RequestBody CinemaRequestDTO cinema) {
        return cinemaService.createCinema(cinema);
    }

    @PutMapping("/{id}")
    public CinemaResponseDTO update(
            @PathVariable Long id,
            @RequestBody CinemaRequestDTO cinema) {
        return cinemaService.updateCinema(id, cinema);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
    }

    @GetMapping("/search")
    public List<CinemaResponseDTO> searchByName(String keyword) {
        return cinemaService.searchCinemasByName(keyword);
    }

    @GetMapping("/city")
    public List<CinemaResponseDTO> getByCity(String city) {
        return cinemaService.getCinemasByCity(city);
    }
}
