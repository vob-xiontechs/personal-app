package com.dev.backendapi.io.profile;

import com.dev.backendapi.constants.profile.ProfileConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class ProfileRequest {

    @NotBlank(message = ProfileConstants.NAME_REQUIRED)
    private String name;

    @NotBlank(message = ProfileConstants.EMAIL_REQUIRED)
    @Email(message = ProfileConstants.INVALID_EMAIL_FORMAT)
    private String email;

    @NotBlank(message = ProfileConstants.PASSWORD_REQUIRED)
    @Size(min = ProfileConstants.MIN_PASSWORD_LENGTH, message = ProfileConstants.PASSWORD_TOO_SHORT)
    private String password;

}
