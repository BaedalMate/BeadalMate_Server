package baedalmate.baedalmate.recruit.domain.embed;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Place {
    private String name;// 장소명, 업체명
    private String addressName; // 전체 지번 주소
    private String roadAddressName; // 전체 도로명 주소
    private float x; // X 좌표값 혹은 longitude
    private float y; // Y 좌표값 혹은 latitude

    //== constructor ==//
    private Place() {
    }

    private Place(String name, String addressName, String roadAddressName, float x, float y) {
        this.name = name;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.x = x;
        this.y = y;
    }

    //== 생성 메서드 ==//
    public static Place createPlace(String name, String addressName, String roadAddressName, float x, float y) {
        return new Place(name, addressName, roadAddressName, x, y);
    }
}
