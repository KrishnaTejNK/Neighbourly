package com.dalhousie.Neighbourly.user.dto;

import com.dalhousie.Neighbourly.user.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private boolean isEmailVerified;
    private String contact;
    private Integer neighbourhoodId;
    private String address;
    private UserType userType;
}