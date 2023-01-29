package baedalmate.baedalmate.user.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Fcm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_id")
    private Long id;

    private String deviceCode;
    private String fcmToken;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    //== constructor ==//
    public Fcm() {}

    public Fcm(String fcmToken, String deviceCode) {
        this.fcmToken = fcmToken;
        this.deviceCode = deviceCode;
    }

    //== 생성 메서드 ==//
    public static Fcm createFcm(User user, String fcmToken, String deviceCode){
        Fcm fcm = new Fcm(fcmToken, deviceCode);
        user.addFcm(fcm);
        return fcm;
    }

    //== setter ==//
    public void setUser(User user) {
        this.user = user;
    }
}
