package com.mazid.electronic.store.controllers;

import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.services.FileService;
import com.mazid.electronic.store.utility.ApiResponseMessage;
import com.mazid.electronic.store.dataTransferObjects.UserDto;
import com.mazid.electronic.store.utility.ImageResponse;
import com.mazid.electronic.store.utility.PageableResponse;
import com.mazid.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User APIs", description = "crete, update, delete, get all users, get user by id")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;


    //create user
    @Operation(summary = "create user", description = "You can create user but don't pass the user id in the request body, we will generate the random user id in backend.")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        UserDto user = userService.createUser(userDto);

        return new  ResponseEntity<>(user, HttpStatus.CREATED);
    }

    //update user
    @Operation(summary = "update user", description = "To update user. you need to pass the user id in the url")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String userId,
           @Valid @RequestBody UserDto userDto )
    {

        return new ResponseEntity<>(userService.updateUser(userDto, userId),HttpStatus.OK);
    }

    //delete user
    @DeleteMapping("/{userId}")
    @Operation(summary = "delete user", description = "To delete user. you need to pass the user id in the url")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("User deleted successfully !!").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    //get all users
    @GetMapping
    @Operation(summary = "get all users", description = "To get all users")
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0",required = false ) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "name",required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = "asc",required = false) String sortDir
    ){

        return new ResponseEntity<>(userService.getAllUsers(pageNumber,pageSize,sortBy,sortDir),HttpStatus.OK);
    }

    //get user by id
    @GetMapping("/{userId}")
    @Operation(summary = "get user by id", description = "To get user by id. you need to pass the user id in the url")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId){

        return new ResponseEntity<>(userService.getUserById(userId),HttpStatus.OK);
    }

    //get user by email
    @GetMapping("/email/{email}")
    @Operation(summary = "get user by email", description = "To get user by email. you need to pass the user email in the url")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){

        return new ResponseEntity<>(userService.getUserByEmail(email),HttpStatus.OK);
    }

    //search user
    @GetMapping("/search/{keyword}")
    @Operation(summary = "search user", description = "To search user. you need to pass the search keyword in the url")
    public ResponseEntity<List<UserDto>> searchUserByKeyword(@PathVariable String keyword){

        return new ResponseEntity<>(userService.searchUser(keyword),HttpStatus.OK);
    }


    //upload user profile image

    @PostMapping("/image/{userId}")
    @Operation(summary = "upload user profile image", description = "To upload user profile image. you need to pass the user id in the url")
    public ResponseEntity<ImageResponse> uploadUserProfileImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId) {

        // Check if the user exists
        UserDto user = userService.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found.");
        }

        // Proceed with the file upload
        String imageName = fileService.uploadFile(image, imageUploadPath, userId);

        // Set new image name to user
        user.setImageName(imageName);
        // Update user
        UserDto userDto = userService.updateUser(user, userId);

        // Create response
        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .message("Image uploaded successfully !!")
                .status(HttpStatus.CREATED)
                .success(true)
                .build();

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }


    // serve user image
    @GetMapping("/image/{userId}")
    @Operation(summary = "download user image", description = "To download user image. you need to pass the user id in the url")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
         //get user
        UserDto user = userService.getUserById(userId);
        //get image name
        String imageName = user.getImageName();
        //serve image
        InputStream resource = fileService.getResource(imageUploadPath, imageName);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource,response.getOutputStream());
    }
}
