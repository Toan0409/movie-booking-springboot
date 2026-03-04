package com.cinema.movie_booking.dto.director;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DirectorRequestDTO {
    @NotBlank(message = "Tên đạo diễn không được để trống")
    private String name;

    private String biography;
    private LocalDate dateOfBirth;

    private String nationality;

    private String imageUrl;
}
