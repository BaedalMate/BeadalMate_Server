package baedalmate.baedalmate.domain;

import org.springframework.http.HttpMethod;

public enum Dormitory {
    BURAM("불암"),
    KB("KB"),
    SUNGLIM("성림"),
    SULIM("수림"),
    NURI("누리");

    final private String name;
    public String getName() {
        return name;
    }

    private Dormitory(String name) {
        this.name = name;
    }
}
