package com.cinema.movie_booking.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cinema.movie_booking.dto.api.ApiResponse;
import com.cinema.movie_booking.dto.booking.BookingResponseDTO;
import com.cinema.movie_booking.mapper.BookingMapper;
import com.cinema.movie_booking.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class BookingAdminController {

    private final BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getAllBookings() {

        List<BookingResponseDTO> bookings = bookingRepository.findAll()
                .stream()
                .map(BookingMapper::toBookingResponseDTO)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success(bookings, "Lấy danh sách đặt vé thành công"));
    }
}