package baedalmate.baedalmate.recruit.domain.embed;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String street;
    private String zipcode;
    private String detail;
}
