package baedalmate.baedalmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ImageResponse {
    private List<String> images;
    public ImageResponse() {
        this.images = new ArrayList<>();
    }
}