package com.cinema.movie_booking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.movie_booking.dto.actor.ActorRequestDTO;
import com.cinema.movie_booking.dto.actor.ActorResponseDTO;
import com.cinema.movie_booking.service.ActorService;

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
@RequestMapping("/api/actors")
@AllArgsConstructor
public class ActorController {
    private final ActorService actorService;

    @PostMapping
    public ActorResponseDTO createActor(@Valid @RequestBody ActorRequestDTO actorRequestDTO) {
        return actorService.createActor(actorRequestDTO);
    }

    @GetMapping
    public Page<ActorResponseDTO> getAll(@RequestParam(required = false) String keyword, Pageable pageable) {
        return actorService.getAllActors(keyword, pageable);
    }

    @GetMapping("/{id}")
    public ActorResponseDTO getById(@PathVariable Long id) {
        return actorService.getActorById(id);
    }

    @PutMapping("/{id}")
    public ActorResponseDTO update(@PathVariable Long id, @Valid @RequestBody ActorRequestDTO actorRequestDTO) {
        return actorService.updateActor(id, actorRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        actorService.deleteActor(id);
    }

}
