package com.orange.porfolio.orange.portfolio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.exceptions.ServiceUnavailableRuntimeException;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageUploadServiceTest {
  @Value("${client_id_imgur}")
  private String client_id_imgur;

  @Mock
  private OkHttpClient okHttpClient;

  @InjectMocks
  private ImageUploadService imageUploadService;


  AutoCloseable autoCloseable;
  TestUtilsMocks mocksObjects;
  @BeforeEach
  void setup(){
    mocksObjects = new TestUtilsMocks();
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should upload an image successfully")
  public void uploadImageSuccessfully() throws IOException {
    MockMultipartFile mockMultipartFileImage = mocksObjects.mockMultipartFileImage;
    ImgurResponse mockImgurResponse = mocksObjects.mockImgurResponse;

    Call mockCall = mock(Call.class);
    OkHttpClient.Builder mockBuilder = mock(OkHttpClient.Builder.class);

    when(okHttpClient.newBuilder()).thenReturn(mockBuilder);
    when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(mockImgurResponse);

    Response response = new Response.Builder()
            .request(new Request.Builder().url("https://example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("Upload Successful!")
            .body(ResponseBody.create(MediaType.parse("text/plain"), json))
            .build();

    when(mockCall.execute()).thenReturn(response);

    ImgurResponse imgurResponse = imageUploadService.uploadImage(mockMultipartFileImage);

    assertEquals(mockImgurResponse.getData().getLink(), imgurResponse.getData().getLink());
  }


  @Test
  @DisplayName("Should TRY to upload an image and throw an exception")
  public void uploadImageFailure() throws IOException {
    MockMultipartFile mockMultipartFileImage = mocksObjects.mockMultipartFileImage;
    ImgurResponse mockImgurResponse = mocksObjects.mockImgurResponse;

    Call mockCall = mock(Call.class);
    OkHttpClient.Builder mockBuilder = mock(OkHttpClient.Builder.class);

    when(okHttpClient.newBuilder()).thenReturn(mockBuilder);
    when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(mockImgurResponse);

    Response response = new Response.Builder()
            .request(new Request.Builder().url("https://example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(400)
            .message("Upload Successful!")
            .body(ResponseBody.create(MediaType.parse("text/plain"), json))
            .build();

    when(mockCall.execute()).thenReturn(response);

    Exception exception = assertThrowsExactly(ServiceUnavailableRuntimeException.class, () -> imageUploadService.uploadImage(mockMultipartFileImage)) ;

    assertEquals("Falha ao fazer o upload da imagem", exception.getMessage());
  }
}