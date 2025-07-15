package com.example.chatbot_used_market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Configuration
public class WebClientConfig {
  @Value("${geocodingAPIUrl}")
  private String googleGeocodingAPIUrl;

  @Value("${geocodingAPIKey}")
  private String googleGeocodingAPIKey;

  @Bean
  public WebClient googleGeocodingClient(WebClient.Builder builder){
    return builder
            .baseUrl(googleGeocodingAPIUrl)
            .defaultUriVariables(Collections.singletonMap("apiKey", googleGeocodingAPIKey))
            .defaultHeaders(httpHeaders -> {
              httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            })
            .build();
  }
}
