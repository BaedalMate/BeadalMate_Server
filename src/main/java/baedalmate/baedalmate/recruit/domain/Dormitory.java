package baedalmate.baedalmate.recruit.domain;

import org.springframework.http.HttpMethod;

public enum Dormitory {
    BURAM("불암학사"),
    KB("KB학사"),
    SUNGLIM("성림학사"),
    SULIM("수림학사"),
    NURI("누리학사");

    final private String name;

    public String getName() {
        return name;
    }

    private Dormitory(String name) {
        this.name = name;
    }
}
