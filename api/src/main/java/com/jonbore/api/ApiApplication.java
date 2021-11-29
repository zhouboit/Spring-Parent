package com.jonbore.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/11/27
 */
@RestController
@SpringBootApplication
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @PostMapping(value = "queryImportData", produces = MediaType.MULTIPART_FORM_DATA_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> queryImportData(@RequestParam(value = "file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", file.getName());
        return result;
    }
}
