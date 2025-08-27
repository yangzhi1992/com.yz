package com.commons.controller;

import com.commons.api.retrofit.Response;
import com.commons.api.CommonApiTest;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CommonApiController {

    @Autowired
    @Qualifier("commonApiTest")
    private CommonApiTest commonApiTest;

    @PostMapping("/health")
    public String health(@RequestParam String msg) throws IOException {
        Response<String> response = commonApiTest.health()
                                                 .execute();
        return "Message sent: " + response.body();
    }

    @PostMapping(path = "/{key}")
    public String uploadImage(@RequestParam(value = "files", required = false) MultipartFile[] files) {
        try {
            MediaType mediaType = MediaType.parse("text/plain");
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file.getBytes());
                MultipartBody.Part filePart =
                        MultipartBody.Part.createFormData("file", file.getOriginalFilename(), requestBody);
                commonApiTest.post_upload_part("name", filePart,
                                     RequestBody.create(mediaType, "value"),
                                     RequestBody.create(mediaType, "value1"))
                             .execute()
                             .body();
            }

        } catch (Exception e) {

        }
        return null;
    }
}