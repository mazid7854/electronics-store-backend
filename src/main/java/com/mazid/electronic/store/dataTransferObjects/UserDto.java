package com.mazid.electronic.store.dataTransferObjects;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String userId;

 @Size(min = 4,max = 20,message = "name must be between 4 and 20 characters")
    private String name;

 @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",message = "email must be valid")
    private String email;

 @NotBlank
    private String password;

 @Size(min =4,max = 6, message = "gender must be male or female")
    private String gender;

 @NotBlank(message = "about cannot be empty")
    @Size(min = 20,max = 100, message = "about must be between 10 and 1000 characters")
    private String about;


    private String imageName;

    private List<RoleDto> roles= new ArrayList<>();
}
