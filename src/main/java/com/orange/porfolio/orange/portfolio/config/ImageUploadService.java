package com.orange.porfolio.orange.portfolio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.exceptions.ServiceUnavailableRuntimeException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageUploadService {
  @Value("${client_id_imgur}")
  private String client_id_imgur;

  public ImgurResponse uploadImage(MultipartFile file)  {
    try{
    String url = "https://api.imgur.com/3/image";
    OkHttpClient client = new OkHttpClient().newBuilder()
            .build();

    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("image", file.getOriginalFilename(),
            RequestBody.create(MediaType.parse("application/octet-stream"), file.getBytes()))
            .addFormDataPart("type","image")
            .build();

    Request request = new Request.Builder()
            .url(url)
            .method("POST", body)
            .addHeader("Authorization", "Client-ID " + client_id_imgur)
            .build();

    Response response = client.newCall(request).execute();

      if (!response.isSuccessful())
        throw new ServiceUnavailableRuntimeException("Falha ao fazer o upload da imagem");

      ObjectMapper objectMapper = new ObjectMapper();
      ImgurResponse imgurResponse = objectMapper.readValue(response.body().string(), ImgurResponse.class);

      return imgurResponse;

  }catch (IOException e){
    throw new ServiceUnavailableRuntimeException(e.getMessage());
}


  }
}
