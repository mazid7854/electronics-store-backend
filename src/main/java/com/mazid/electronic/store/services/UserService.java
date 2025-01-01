package com.mazid.electronic.store.services;

import com.mazid.electronic.store.dataTransferObjects.UserDto;
import com.mazid.electronic.store.utility.PageableResponse;

import java.util.List;

public interface UserService {

    //create user

    UserDto createUser(UserDto userDto);

    //update user

    UserDto updateUser(UserDto userDto, String userId);

    //delete user

    void deleteUser(String userId);

    //get all users
    PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get user by id

    UserDto getUserById(String userId);

    //get user by email

    UserDto getUserByEmail(String email);

    //search user

    List<UserDto> searchUser(String keyword);


}
