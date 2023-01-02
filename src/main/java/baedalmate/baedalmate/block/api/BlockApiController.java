package baedalmate.baedalmate.block.api;

import baedalmate.baedalmate.block.dto.BlockRequestDto;
import baedalmate.baedalmate.block.service.BlockService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import baedalmate.baedalmate.swagger.ResultSuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "차단 api")
@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})public class BlockApiController {

    private final BlockService blockService;

    @Operation(description = "차단하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "이미 차단한 유저",
                                            value = "{\"code\": 400, \"message\": \"Already blocked\"}"),
                            }
                    )),
    })
    @PostMapping("/block")
    public ResponseEntity<Map> block(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid BlockRequestDto blockRequestDto
    ) {
        blockService.block(principalDetails.getId(), blockRequestDto.getUserId());
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }

    @Operation(description = "차단 해제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "차단하지 않은 유저",
                                            value = "{\"code\": 400, \"message\": \"Target is not blocked\"}"),
                            }
                    )),
    })
    @PostMapping("/unblock")
    public ResponseEntity<Map> unblock(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid BlockRequestDto blockRequestDto
    ) {
        blockService.unblock(principalDetails.getId(), blockRequestDto.getUserId());
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }
}
