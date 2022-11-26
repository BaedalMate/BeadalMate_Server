package baedalmate.baedalmate.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ImageDto {
    private List<String> images;

    public ImageDto() {
        this.images = new ArrayList<>();
    }
}