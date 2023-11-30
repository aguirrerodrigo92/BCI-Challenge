package com.bci.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Request for {@link com.bci.challenge.model.Phone}
 */
@Data
public class PhoneDto implements Serializable {
    private long number;
    @JsonProperty(value = "city_code")
    private int cityCode;
    @JsonProperty(value = "country_code")
    private String countryCode;
}