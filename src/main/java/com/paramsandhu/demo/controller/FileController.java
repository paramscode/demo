package com.paramsandhu.demo.controller;

import com.paramsandhu.demo.dto.FileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RestController
public class FileController {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

     @PostMapping("/api/file")
     public String uploadFile(@RequestBody FileRequest fileRequest) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileRequest.getFilename())
                    .build();

         s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromString(fileRequest.getPayload()));



         return String.format("File %s uploaded successfully", fileRequest.getFilename());
     }

}
