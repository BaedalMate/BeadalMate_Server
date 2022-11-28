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
    private int total;
    private int number;
    private List<ParticipantMenuDto> participants;
    private int myPrice;
    private int shippingFee;
    private int coupon;
}
