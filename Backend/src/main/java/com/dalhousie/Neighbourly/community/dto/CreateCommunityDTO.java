package com.dalhousie.Neighbourly.community.dto;

import lombok.Data;

@Data
public class CreateCommunityDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String pincode;
}
