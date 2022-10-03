package baedalmate.baedalmate.api;

import baedalmate.baedalmate.errors.exceptions.ImageNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Api(tags = "이미지 api")
@RestController
public class ImageApiController {

    @Value("${spring.servlet.multipart.location}")
    private String path;

    @ApiOperation(value = "이미지 요청")
    @GetMapping(value = "/images/{fileOriginName}")
    public ResponseEntity<Resource> getImageByName(@PathVariable("fileOriginName") String fileName) {
        try {
            FileSystemResource resource = new FileSystemResource(path + "/" + fileName);
            if (!resource.exists()) {
                throw new ImageNotFoundException();
            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = Paths.get(path + "/" + fileName);
            header.add("Content-Type", Files.probeContentType(filePath));
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        } catch (Exception e) {
            throw new ImageNotFoundException();
        }
    }
}
