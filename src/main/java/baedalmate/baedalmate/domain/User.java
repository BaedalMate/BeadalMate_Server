package baedalmate.baedalmate.domain;

import baedalmate.baedalmate.domain.embed.Address;
import baedalmate.baedalmate.oauth.SocialType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@Data
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String name;
    private String password;
    private String email;
    private String nickname;
    @Setter
    private String role;
    private String provider;
    private String providerId;
    private String profileImage;

    @Embedded
    private Address address;

    private SocialType socialType;
    private String socialId;

    private Dormitory dormitory;

    @CreationTimestamp
    private Timestamp createDate;
    @UpdateTimestamp
    private Timestamp updateDate;

    public String getAddress() {
        if(address == null) {
            return "";
        }
        if(address.getStreet() == null || address.getDetail() == null || address.getZipcode() == null){
            return "";
        }
        return address.getStreet() + " " + address.getDetail();
    }

    public String getDormitory() {
        if(dormitory == null) {
            return "";
        }
        return dormitory.getName();
    }
}
