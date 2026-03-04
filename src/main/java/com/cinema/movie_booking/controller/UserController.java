package com.cinema.movie_booking.controller;

import com.cinema.movie_booking.dto.user.UserRequestDTO;
import com.cinema.movie_booking.dto.user.UserResponseDTO;
import com.cinema.movie_booking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Tao nguoi dung moi
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO user = userService.createUser(requestDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Lay danh sach tat ca nguoi dung (phan trang)
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @PageableDefault(size = 10, sort = "userId") Pageable pageable) {
        Page<UserResponseDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Lay thong tin nguoi dung theo ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Cap nhat thong tin nguoi dung
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO user = userService.updateUser(id, requestDTO);
        return ResponseEntity.ok(user);
    }

    /**
     * Xoa nguoi dung (vo hieu hoa)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Kich hoat nguoi dung
     * PATCH /api/users/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable Long id) {
        UserResponseDTO user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Vo hieu hoa nguoi dung
     * PATCH /api/users/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable Long id) {
        UserResponseDTO user = userService.deactivateUser(id);
        return ResponseEntity.ok(user);
    }
}
