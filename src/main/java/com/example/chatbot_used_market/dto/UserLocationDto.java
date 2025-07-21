package com.example.chatbot_used_market.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLocationDto {
  @NotBlank private String location;
  @NotBlank private String dongName;

  @NotNull
  @DecimalMin(value = "-90.0")
  @DecimalMax(value = "90.0")
  double latitude;

  @NotNull
  @DecimalMin(value = "-180.0")
  @DecimalMax(value = "180.0")
  double longitude;
}
