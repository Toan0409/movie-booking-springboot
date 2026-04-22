package com.cinema.movie_booking.service.impl;

import com.cinema.movie_booking.dto.chatbot.ChatRequestDTO;
import com.cinema.movie_booking.dto.chatbot.ChatResponseDTO;
import com.cinema.movie_booking.dto.movie.MovieResponseDTO;
import com.cinema.movie_booking.entity.Movie;
import com.cinema.movie_booking.mapper.MovieMapper;
import com.cinema.movie_booking.repository.MovieRepository;
import com.cinema.movie_booking.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request, Pageable pageable) {
        String rawMessage = request != null && request.getMessage() != null ? request.getMessage() : "";
        String message = rawMessage.toLowerCase(Locale.ROOT).trim();

        if (message.isBlank()) {
            return politeFallback();
        }

        List<Movie> movies;
        String botMessage;

        if (containsAny(message, "hành động", "action", "hanh dong")) {
            movies = recommendByGenre("action");
            botMessage = "Đây là các phim hành động hay dành cho bạn:";
        } else if (containsAny(message, "tình cảm", "romance", "lang man", "lãng mạn")) {
            movies = recommendByGenre("romance");
            botMessage = "Đây là các phim tình cảm đang chiếu:";
        } else if (containsAny(message, "tối nay", "hôm nay", "toi nay", "hom nay")) {
            movies = recommendTonight();
            botMessage = "Các phim phù hợp để xem tối nay:";
        } else if (containsAny(message, "gia đình", "family", "tre em", "trẻ em")) {
            movies = recommendFamily();
            botMessage = "Gợi ý phim phù hợp đi cùng gia đình:";
        } else if (containsAny(message, "kinh dị", "horror")) {
            movies = recommendByGenre("horror");
            botMessage = containsAny(message, "top") ? "Top phim kinh dị nổi bật:" : "Các phim kinh dị bạn có thể quan tâm:";
        } else if (containsAny(message, "top")) {
            movies = recommendTop();
            botMessage = "Top phim nổi bật hiện tại:";
        } else {
            return politeFallback();
        }

        List<MovieResponseDTO> responseMovies = movies.stream()
                .limit(resolveLimit(pageable))
                .map(movieMapper::toResponseDTO)
                .collect(Collectors.toList());

        if (responseMovies.isEmpty()) {
            return ChatResponseDTO.builder()
                    .movies(List.of())
                    .message("Hiện chưa có phim phù hợp với yêu cầu của bạn. Bạn thử thể loại khác nhé!")
                    .build();
        }

        return ChatResponseDTO.builder()
                .movies(responseMovies)
                .message(botMessage)
                .build();
    }

    private List<Movie> recommendByGenre(String genreKeyword) {
        return movieRepository.findByIsDeletedFalse().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsNowShowing()))
                .filter(m -> m.getGenre() != null && m.getGenre().getName() != null)
                .filter(m -> m.getGenre().getName().toLowerCase(Locale.ROOT).contains(genreKeyword))
                .sorted(byRatingDescThenReleaseDateDesc())
                .collect(Collectors.toList());
    }

    private List<Movie> recommendTonight() {
        return movieRepository.findByIsNowShowingTrueAndIsDeletedFalse().stream()
                .sorted(byRatingDescThenReleaseDateDesc())
                .collect(Collectors.toList());
    }

    private List<Movie> recommendFamily() {
        return movieRepository.findByIsDeletedFalse().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsNowShowing()))
                .filter(m -> {
                    String age = m.getAgeRating() == null ? "" : m.getAgeRating().toUpperCase(Locale.ROOT);
                    return age.equals("G") || age.equals("PG") || age.equals("P");
                })
                .sorted(byRatingDescThenReleaseDateDesc())
                .collect(Collectors.toList());
    }

    private List<Movie> recommendTop() {
        return movieRepository.findByIsNowShowingTrueAndIsDeletedFalse().stream()
                .sorted(byRatingDescThenReleaseDateDesc())
                .collect(Collectors.toList());
    }

    private Comparator<Movie> byRatingDescThenReleaseDateDesc() {
        return Comparator
                .comparing((Movie m) -> m.getRating() == null ? 0.0 : m.getRating(), Comparator.reverseOrder())
                .thenComparing(m -> m.getReleaseDate() == null ? LocalDate.MIN : m.getReleaseDate(), Comparator.reverseOrder());
    }

    private boolean containsAny(String text, String... keywords) {
        return Arrays.stream(keywords).anyMatch(text::contains);
    }

    private int resolveLimit(Pageable pageable) {
        if (pageable == null) {
            return 5;
        }
        int size = pageable.getPageSize();
        if (size <= 0) {
            return 5;
        }
        return Math.min(size, 10);
    }

    private ChatResponseDTO politeFallback() {
        return ChatResponseDTO.builder()
                .movies(List.of())
                .message("Xin lỗi, tôi hiện hỗ trợ tư vấn phim đang chiếu theo thể loại (hành động, tình cảm, kinh dị), phim gia đình, phim tối nay và top phim. Bạn thử hỏi theo các chủ đề này nhé.")
                .build();
    }
}
