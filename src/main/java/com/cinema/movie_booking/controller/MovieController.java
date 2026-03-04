package com.cinema.movie_booking.controller;

import com.cinema.movie_booking.dto.api.ApiResponse;
import com.cinema.movie_booking.dto.movie.MoviePageDTO;
import com.cinema.movie_booking.dto.movie.MovieRequestDTO;
import com.cinema.movie_booking.dto.movie.MovieResponseDTO;
import com.cinema.movie_booking.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    /**
     * Tạo mới Movie
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponseDTO>> createMovie(
            @Valid @RequestBody MovieRequestDTO requestDTO) {
        MovieResponseDTO movie = movieService.createMovie(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(movie, "Tạo phim thành công"));
    }

    /**
     * Lấy danh sách tất cả Movies (có phân trang)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> getAllMovies(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<MovieResponseDTO> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(ApiResponse.success(movies, "Lấy danh sách phim thành công"));
    }

    /**
     * Lấy Movie theo id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> getMovieById(@PathVariable Long id) {
        MovieResponseDTO movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.success(movie, "Lấy thông tin phim thành công"));
    }

    /**
     * Cập nhật Movie
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequestDTO requestDTO) {
        MovieResponseDTO movie = movieService.updateMovie(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(movie, "Cập nhật phim thành công"));
    }

    /**
     * Xóa Movie (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa phim thành công"));
    }

    /**
     * Khôi phục phim đã xóa
     */
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> restoreMovie(@PathVariable Long id) {
        MovieResponseDTO movie = movieService.restoreMovie(id);
        return ResponseEntity.ok(ApiResponse.success(movie, "Khôi phục phim thành công"));
    }

    /**
     * Lấy danh sách phim đang chiếu (có phân trang)
     */
    @GetMapping("/now-showing")
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> getMoviesNowShowing(
            @PageableDefault(size = 10, sort = "releaseDate") Pageable pageable) {
        Page<MovieResponseDTO> movies = movieService.getMoviesNowShowing(pageable);
        return ResponseEntity.ok(ApiResponse.success(movies, "Lấy danh sách phim đang chiếu thành công"));
    }

    /**
     * Lấy danh sách phim sắp chiếu (có phân trang)
     */
    @GetMapping("/coming-soon")
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> getMoviesComingSoon(
            @PageableDefault(size = 10, sort = "releaseDate") Pageable pageable) {
        Page<MovieResponseDTO> movies = movieService.getMoviesComingSoon(pageable);
        return ResponseEntity.ok(ApiResponse.success(movies, "Lấy danh sách phim sắp chiếu thành công"));
    }

    /**
     * Lấy danh sách phim nổi bật (có phân trang)
     */
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> getFeaturedMovies(
            @PageableDefault(size = 10, sort = "rating") Pageable pageable) {
        Page<MovieResponseDTO> movies = movieService.getFeaturedMovies(pageable);
        return ResponseEntity.ok(ApiResponse.success(movies, "Lấy danh sách phim nổi bật thành công"));
    }

    /**
     * Tìm kiếm phim theo tên (có phân trang)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> searchMovies(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<MovieResponseDTO> movies = movieService.searchMovies(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(movies, "Tìm kiếm phim thành công"));
    }

    /**
     * Lấy danh sách phim theo thể loại (có phân trang)
     */
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> getMoviesByGenre(
            @PathVariable Long genreId,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<MovieResponseDTO> movies = movieService.getMoviesByGenre(genreId, pageable);
        return ResponseEntity.ok(ApiResponse.success(movies, "Lấy danh sách phim theo thể loại thành công"));
    }
}
