package com.bci.challenge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Request DTO for {@link com.bci.challenge.model.User}
 */
@Data
public class UserRequest implements Serializable {
    String name;
    String email;
    String password;
    List<PhoneDto> phones;
}