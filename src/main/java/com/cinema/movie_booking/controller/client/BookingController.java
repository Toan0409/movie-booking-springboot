package com.cinema.movie_booking.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.movie_booking.dto.booking.BookingRequestDTO;
import com.cinema.movie_booking.dto.booking.BookingResponseDTO;
import com.cinema.movie_booking.service.BookingService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestParam Long userId,
            @RequestBody BookingRequestDTO request) {

        BookingResponseDTO response = bookingService.createBooking(userId, request);

        return ResponseEntity.ok(response);
    }
}
