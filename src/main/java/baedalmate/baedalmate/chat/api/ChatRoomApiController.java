package baedalmate.baedalmate.chat.api;

import baedalmate.baedalmate.chat.dto.*;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.chat.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = {"채팅방 조회 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    // 모든 채팅방 목록 반환
    @ApiOperation(value = "채팅방 전체 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한")
    })
    @GetMapping("/rooms")
    public ResponseEntity<ChatRoomListDto> getRooms(
            @AuthUser PrincipalDetails principalDetails
    ) {
        ChatRoomListDto chatRoomList = chatRoomService.getChatRoomList(principalDetails.getId());
        return ResponseEntity.ok().body(chatRoomList);
    }

    // 특정 채팅방 조회
    @ApiOperation(value = "특정 채팅방 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한")
    })
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetail(@PathVariable Long roomId) {
        ChatRoomDetailDto chatRoomDetailDto = chatRoomService.getChatRoomDetail(roomId);
        return ResponseEntity.ok().body(chatRoomDetailDto);
    }
}
