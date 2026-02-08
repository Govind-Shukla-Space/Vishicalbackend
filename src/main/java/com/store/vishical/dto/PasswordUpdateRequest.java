package com.store.vishical.dto;

import lombok.Data;

@Data
public class PasswordUpdateRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
