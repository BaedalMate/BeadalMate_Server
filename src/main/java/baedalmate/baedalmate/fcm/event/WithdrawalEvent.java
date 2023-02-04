package baedalmate.baedalmate.fcm.event;

import baedalmate.baedalmate.user.domain.Fcm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class WithdrawalEvent {
    private final Long chatRoomId;
    private final String title;
    private final String description;
    private final String image;
    private final List<Fcm> fcmList;
}