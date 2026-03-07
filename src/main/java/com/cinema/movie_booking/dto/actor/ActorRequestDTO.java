package com.cinema.movie_booking.dto.actor;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActorRequestDTO {
    @NotBlank(message = "Ten diễn viên không được để trống")
    private String name;

    private String biography;
    private LocalDate birthDate;
    private String nationality;
    private String imageUrl;
}
