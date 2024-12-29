package com.paramsandhu.demo.controller;

import com.paramsandhu.demo.dto.FileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

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

     @GetMapping("/api/files")
     public List<String> listFiles() {
         try {
             ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                     .bucket(bucketName)
                     .build();

             ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
             
             return listObjectsV2Response.contents().stream()
                     .map(S3Object::key)
                     .collect(Collectors.toList());
         } catch (Exception e) {
             // Log the exception and return empty list
             return List.of();
         }
     }

     @GetMapping("/api/files/{filename}")
     public ResponseEntity<String> getFile(@PathVariable String filename) {
         try {
             GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                     .bucket(bucketName)
                     .key(filename)
                     .build();

             ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
             
             return ResponseEntity.ok()
                     .contentType(MediaType.parseMediaType(response.response().contentType()))
                     .body(new String(response.readAllBytes()));
         } catch (NoSuchKeyException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                     .body("File not found: " + filename);
         } catch (S3Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("Error retrieving file: " + e.getMessage());
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("Unexpected error: " + e.getMessage());
         }
     }

     @GetMapping("/api/files/{filename}/download")
     public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
         try {
             GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                     .bucket(bucketName)
                     .key(filename)
                     .build();

             ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
             
             InputStreamResource resource = new InputStreamResource(response);
             
             return ResponseEntity.ok()
                     .contentType(MediaType.parseMediaType(response.response().contentType()))
                     .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                     .body(resource);
         } catch (NoSuchKeyException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
         } catch (S3Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         }
     }

     @GetMapping("/api/files/{filename}/presigned-url")
     public ResponseEntity<String> getPresignedUrl(@PathVariable String filename) {
         try {
             S3Presigner presigner = S3Presigner.create();
             
             GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                     .bucket(bucketName)
                     .key(filename)
                     .build();

             GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                     .signatureDuration(Duration.ofMinutes(5)) // URL valid for 5 minutes
                     .getObjectRequest(getObjectRequest)
                     .build();

             PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
             String url = presignedRequest.url().toString();
             
             return ResponseEntity.ok(url);
         } catch (NoSuchKeyException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                     .body("File not found: " + filename);
         } catch (S3Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("Error generating presigned URL: " + e.getMessage());
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("Unexpected error: " + e.getMessage());
         }
     }
}
