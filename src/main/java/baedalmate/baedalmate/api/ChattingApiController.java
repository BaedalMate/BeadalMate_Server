package baedalmate.baedalmate.api;

import baedalmate.baedalmate.dto.ChatRoom;
import baedalmate.baedalmate.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChattingApiController {

    private final ChattingService chattingService;

    // 모든 채팅방 목록 반환
    @GetMapping("/chat/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return chattingService.findAllRoom();
    }

    // 채팅방 생성
    @PostMapping("/chat/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam Long id, @RequestParam String name) {
        return chattingService.createRoom(id, name);
    }

    // 특정 채팅방 조회
    @GetMapping("/chat/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable Long roomId) {
        return chattingService.findById(roomId);
    }

}