package com.cinema.movie_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinema.movie_booking.entity.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
