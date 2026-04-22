package com.cinema.movie_booking.controller.client;

import com.cinema.movie_booking.dto.api.ApiResponse;
import com.cinema.movie_booking.dto.chatbot.ChatRequestDTO;
import com.cinema.movie_booking.dto.chatbot.ChatResponseDTO;
import com.cinema.movie_booking.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponseDTO>> chat(
            @RequestBody ChatRequestDTO request,
            @PageableDefault(size = 5) Pageable pageable) {
        ChatResponseDTO response = chatbotService.chat(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Chatbot trả lời thành công"));
    }
}
