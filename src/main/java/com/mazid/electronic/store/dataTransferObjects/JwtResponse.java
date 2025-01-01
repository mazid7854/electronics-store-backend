package com.mazid.electronic.store.dataTransferObjects;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String token;
    UserDto user;
    private String refreshToken;
}
