package com.cinema.movie_booking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.movie_booking.dto.director.DirectorRequestDTO;
import com.cinema.movie_booking.dto.director.DirectorResponseDTO;
import com.cinema.movie_booking.service.DirectorService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/directors")
@AllArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public DirectorResponseDTO createDirector(@Valid @RequestBody DirectorRequestDTO requestDTO) {
        // TODO: process POST request
        return directorService.createDirector(requestDTO);
    }

    @GetMapping
    public Page<DirectorResponseDTO> getAll(@RequestParam(required = false) String keyword,
            Pageable pageable) {
        return directorService.getAllDirectors(keyword, pageable);
    }

    @GetMapping("/{id}")
    public DirectorResponseDTO getById(@PathVariable Long id) {
        return directorService.getDirectorById(id);
    }

    @PutMapping("/{id}")
    public DirectorResponseDTO update(@PathVariable Long id, @Valid @RequestBody DirectorRequestDTO requestDTO) {
        // TODO: process PUT request
        return directorService.updateDirector(id, requestDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        directorService.deleteDirector(id);
    }
}
