package com.cinema.movie_booking.dto.cinema;

import lombok.Data;

@Data
public class CinemaRequestDTO {
    private String name;

    private String address;

    private String city;

    private String district;

    private String phone;

    private String email;

    private String imageUrl;

    private String description;

    private Boolean isActive;
}