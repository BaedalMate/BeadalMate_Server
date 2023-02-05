package baedalmate.baedalmate.notification.api;

import baedalmate.baedalmate.notification.dto.NotificationDto;
import baedalmate.baedalmate.notification.dto.NotificationListDto;
import baedalmate.baedalmate.notification.service.NotificationService;
import baedalmate.baedalmate.recruit.dto.RecruitListWithLastAndCount;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.CustomPageableAsQueryParam;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "알림 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class NotificationApiController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = NotificationListDto.class))),
    })
    @GetMapping(value = "/notification")
    public ResponseEntity<NotificationListDto> notifications(
            @AuthUser PrincipalDetails principalDetails
            ) {
        List<NotificationDto> notificationDtos = notificationService.notificationList(principalDetails.getId());
        NotificationListDto response = new NotificationListDto(notificationDtos);
        return ResponseEntity.ok().body(response);
    }
}
