package com.example.chatbot_used_market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Configuration
public class WebClientConfig {
  @Value("${geocodingAPIUrl}")
  private String googleGeocodingAPIUrl;

  @Value("${geocodingAPIKey}")
  private String googleGeocodingAPIKey;

  @Bean
  public WebClient googleGeocodingClient(){
    return WebClient.builder()
            .baseUrl(googleGeocodingAPIUrl)
            .defaultUriVariables(Collections.singletonMap("apiKey", googleGeocodingAPIKey))
            .defaultHeaders(httpHeaders -> {
              httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            })
            .build();
  }

  // logging when request to google geocoding request
  public static ExchangeFilterFunction logRequest() {
    return (clientRequest, next) -> {
//      log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
      System.out.printf("Request: %s %s \n", clientRequest.method(), clientRequest.url());
//      clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
      clientRequest.headers().forEach((name, values) -> values.forEach(value ->
//              log.info("{}={}", name, value)
                      System.out.printf("%s=%s \n", name, value)
      ));
      return next.exchange(clientRequest);
    };
  }
}
