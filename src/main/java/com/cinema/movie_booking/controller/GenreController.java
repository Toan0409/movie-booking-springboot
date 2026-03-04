package com.cinema.movie_booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.movie_booking.dto.genre.GenreRequestDTO;
import com.cinema.movie_booking.dto.genre.GenreResponseDTO;
import com.cinema.movie_booking.service.GenreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @PostMapping
    public GenreResponseDTO createGenre(@Valid @RequestBody GenreRequestDTO requestDTO) {
        System.out.println("NAME ==================== " + requestDTO.getName());
        return genreService.createGenre(requestDTO);
    }

    @GetMapping
    public Page<GenreResponseDTO> getAll(
            @RequestParam(required = false) String keyword, Pageable pageable) {
        return genreService.getAllGenres(keyword, pageable);
    }

    @GetMapping("/{id}")
    public GenreResponseDTO getByID(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }

    @PutMapping("/{id}")
    public GenreResponseDTO update(@PathVariable Long id, @Valid @RequestBody GenreRequestDTO requestDTO) {
        return genreService.updateGenre(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        genreService.deleteGenre(id);
    }

    @PatchMapping("/{id}/restore")
    public GenreResponseDTO restore(@PathVariable Long id) {
        return genreService.restoreGenre(id);
    }

}
