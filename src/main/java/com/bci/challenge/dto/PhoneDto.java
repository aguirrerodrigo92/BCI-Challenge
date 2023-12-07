package com.bci.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Request for {@link com.bci.challenge.model.Phone}
 */
@Schema(description = "Phone details")
@Data
public class PhoneDto {
    @Schema(description = "Phone number", example = "1234567890")
    private long number;

    @Schema(description = "City code", example = "123")
    @JsonProperty(value = "city_code")
    private int cityCode;

    @Schema(description = "Country code", example = "+1")
    @JsonProperty(value = "country_code")
    private String countryCode;
}