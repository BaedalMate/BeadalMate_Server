package baedalmate.baedalmate.chat.api;

import baedalmate.baedalmate.chat.dto.*;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.chat.service.ChatRoomService;
import baedalmate.baedalmate.chat.service.MessageService;
import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.ErrorDto;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "채팅방 조회 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    // 모든 채팅방 목록 반환
    @Operation(summary = "채팅방 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(
                    responseCode = "401",
//                    description = "1. 테스트1</br>2. 테스트2",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "404-1", description = "Not Found 1 desc", value = "{\"code\": 400, \"message\": \"Token expired\"}"),
                                    @ExampleObject(name = "404-2", description = "Not Found 2 desc")
                            }
                    )),
    })
    @GetMapping("/rooms")
    public ResponseEntity<ChatRoomListDto> getRooms(
            @AuthUser PrincipalDetails principalDetails
    ) {
        ChatRoomListDto chatRoomList = chatRoomService.getChatRoomList(principalDetails.getId());
        return ResponseEntity.ok().body(chatRoomList);
    }

    // 특정 채팅방 조회
    @Operation(summary = "특정 채팅방 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetail(
            @AuthUser PrincipalDetails principalDetails,
            @PathVariable Long roomId
    ) {
        ChatRoomDetailDto chatRoomDetailDto = chatRoomService.getChatRoomDetail(principalDetails.getId(), roomId);
        return ResponseEntity.ok().body(chatRoomDetailDto);
    }
}