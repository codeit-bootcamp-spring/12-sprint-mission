package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AWSS3Test {

    private static S3Client s3Client;
    private static S3Presigner s3Presigner;
    private static String bucketName;

    private static final String TEST_OBJECT_KEY = "test/aws-s3-test.jpg";
    private static final String TEST_IMAGE_PATH = "src/test/resources/test-image.jpg";
    private static final String DOWNLOAD_IMAGE_PATH = "build/downloaded-test-image.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";

    @BeforeAll
    static void setup() throws IOException {
        Properties properties = new Properties(); // env를 담는 객체

        try(FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
        }

        String accessKeyId = getRequiredProperty(properties, "AWS_S3_ACCESS_KEY");
        String secretAccessKey = getRequiredProperty(properties, "AWS_S3_SECRET_KEY");
        String region = getRequiredProperty(properties, "AWS_S3_REGION");
        bucketName = getRequiredProperty(properties, "AWS_S3_BUCKET");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey
        ); // AWS 인증 객체 생성

        StaticCredentialsProvider credentialsProvider =
                StaticCredentialsProvider.create(credentials); // 인증 정보를 클라이언트에 전달할 수 있는 형태로 감쌈

        s3Client = S3Client.builder() // 클라이언트가 사용할 aws 인증 정보 지정
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

        s3Presigner = S3Presigner.builder() //presigned url을 만들 내용지정
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @AfterAll
    static void tearDown(){
        if(s3Client != null) {
            s3Client.close();
        }

        if(s3Presigner != null) {
            s3Presigner.close();
        }
    }

    @Test
    void upload(){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder() // 파일 업로드 요청 객체
                .bucket(bucketName)
                .key(TEST_OBJECT_KEY)
                .contentType(CONTENT_TYPE)
                .build();

        s3Client.putObject( // S3에 객체 업로드
                putObjectRequest, // 어느 버킷에 어떤 이름으로 저장할지
                RequestBody.fromFile(Path.of(TEST_IMAGE_PATH)) // 무슨 내용을 저장할지
        );

        // S3 메타 데이터 확인용 요청 생성
        // 파일 내용을 확인하지 않고 존재하는지 여부를 확인
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(TEST_OBJECT_KEY)
                .build();

        // 객체가 있는지 확인
        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

        // 응답이 null인지 확인
        assertNotNull (headObjectResponse);
        // 업로드된 객체의 content type이 text/plain이 맞는지 확인
        assertEquals(CONTENT_TYPE, headObjectResponse.contentType());

        System.out.println("S3 업로드 성공");
        System.out.println("bucketName = " +bucketName);
        System.out.println("objectKey = " +TEST_OBJECT_KEY);
    }

    @Test
    void download() throws IOException {
        uploadTestFile();

        // 다운로드 요청 객체 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(TEST_OBJECT_KEY)
                .build();

        // byte 형태로 S3으로부터 객체 다운로드
        ResponseBytes<GetObjectResponse> responseBytes =
                s3Client.getObjectAsBytes(getObjectRequest);

        byte[] downloadedBytes = responseBytes.asByteArray();

        Path downloadPath = Path.of(DOWNLOAD_IMAGE_PATH);
        Files.createDirectories(downloadPath.getParent()); //파일 저장 폴더가 없으면 만듦..
        Files.write(downloadPath,downloadedBytes); // 다운받은 byte를 실제 저장 폴더에 저장.

        byte[] originalBytes = Files.readAllBytes(Path.of(TEST_IMAGE_PATH));
        byte[] savedBytes = Files.readAllBytes(downloadPath);

        assertArrayEquals(originalBytes, savedBytes);
        assertTrue(Files.exists(downloadPath));
        assertTrue(Files.size(downloadPath) > 0);

        System.out.println("S3 다운로드 성공");
        System.out.println("downPath = " + DOWNLOAD_IMAGE_PATH);
    }

    @Test
    void createPresignedUrl(){
        uploadTestFile();

        // Presigned URL로 접근할 S3객체를 지정
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(TEST_OBJECT_KEY)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration((Duration.ofMinutes(10))) // 유효시간
                .getObjectRequest(getObjectRequest) // url 생성 대상 객체
                .build();

        URL presignedUrl = s3Presigner
                .presignGetObject(presignRequest)
                .url();

        assertNotNull(presignedUrl);
        assertTrue(presignedUrl.toString().contains(TEST_OBJECT_KEY));

        System.out.println("Presigned URL 생성 성공");
        System.out.println("presignedUrl = " + presignedUrl);
    }

    private void uploadTestFile(){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(TEST_OBJECT_KEY)
                .contentType(CONTENT_TYPE)
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromFile(Path.of(TEST_IMAGE_PATH))
        );
    }


    private static String getRequiredProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if(value == null || value.isBlank()){
            throw new IllegalStateException(".env에" + key + " 값이 없습니다.");
        }

        return value.trim()
                .replace("\"","")
                .replace("'", "");
    }
}
