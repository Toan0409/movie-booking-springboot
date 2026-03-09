package com.cinema.movie_booking.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cinema.movie_booking.dto.theater.TheaterRequestDTO;
import com.cinema.movie_booking.dto.theater.TheaterResponseDTO;
import com.cinema.movie_booking.entity.Cinema;
import com.cinema.movie_booking.entity.Theater;
import com.cinema.movie_booking.mapper.TheaterMapper;
import com.cinema.movie_booking.repository.CinemaRepository;
import com.cinema.movie_booking.repository.TheaterRepository;
import com.cinema.movie_booking.service.TheaterService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final CinemaRepository cinemaRepository;

    @Override
    public TheaterResponseDTO create(TheaterRequestDTO request) {
        Cinema cinema = cinemaRepository.findById(request.getCinemaId())
                .orElseThrow(() -> new RuntimeException("Cinema not found"));
        Theater theater = TheaterMapper.toEntity(request, cinema);
        theater = theaterRepository.save(theater);
        return TheaterMapper.toDTO(theater);
    }

    @Override
    public void delete(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater not found"));
        theater.setIsActive(false);
        theaterRepository.save(theater);
    }

    @Override
    public Page<TheaterResponseDTO> getAll(Pageable pageable) {
        Page<Theater> theaters = theaterRepository.findAll(pageable);
        return theaters.map(TheaterMapper::toDTO);
    }

    @Override
    public TheaterResponseDTO getById(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        return TheaterMapper.toDTO(theater);
    }

    @Override
    public TheaterResponseDTO restore(Long id) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        theater.setIsActive(true);
        theaterRepository.save(theater);
        return TheaterMapper.toDTO(theater);
    }

    @Override
    public TheaterResponseDTO update(Long id, TheaterRequestDTO request) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        theater.setName(request.getName());
        theater.setRowsCount(request.getRowsCount());
        theater.setTotalSeats(request.getRowsCount() * request.getSeatsPerRow());
        theater.setSeatsPerRow(request.getSeatsPerRow());
        theater.setTheaterType(request.getTheaterType());
        theater = theaterRepository.save(theater);
        return TheaterMapper.toDTO(theater);
    }

    @Override
    public Page<TheaterResponseDTO> getByIsActiveTrue(Pageable pageable) {
        Page<Theater> theaters = theaterRepository.findByIsActiveTrue(pageable);
        return theaters.map(TheaterMapper::toDTO);
    }
}
