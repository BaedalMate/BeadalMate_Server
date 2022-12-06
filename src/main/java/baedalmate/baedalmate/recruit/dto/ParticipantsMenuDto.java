package baedalmate.baedalmate.recruit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantsMenuDto {
    @Schema(description = "모든 참여자 총 주문금액")
    private int allOrderTotal;
    @Schema(description = "현재 참가자 수")
    private int number;
    @Schema(description = "참가자별 정보")
    private List<ParticipantMenuDto> participants;
    @Schema(description = "요청자 총 주문금액")
    private int myOrderPrice;
    @Schema(description = "전체 배달비")
    private int shippingFee;
    @Schema(description = "인당 배달비")
    private int shippingFeePerParticipant;
    @Schema(description = "쿠폰 금액")
    private int coupon;
}
