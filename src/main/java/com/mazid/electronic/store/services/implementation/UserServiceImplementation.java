package com.mazid.electronic.store.services.implementation;

import com.mazid.electronic.store.dataTransferObjects.UserDto;
import com.mazid.electronic.store.entities.Role;
import com.mazid.electronic.store.entities.User;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.repositories.RoleRepository;
import com.mazid.electronic.store.services.FileService;
import com.mazid.electronic.store.utility.Helper;
import com.mazid.electronic.store.utility.PageableResponse;
import com.mazid.electronic.store.repositories.UserRepository;
import com.mazid.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${user.profile.image.path}")
    private String imagePath;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    Logger logger = LoggerFactory.getLogger(UserServiceImplementation.class);



    @Override
    public UserDto createUser(UserDto userDto) {
        //generate unique user id
        String userId= UUID.randomUUID().toString();
        userDto.setUserId(userId);


        //convert dto to entity
        User user= dtoToEntity(userDto);

        //encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

         //assign role to user
        Role role = new Role();
        role.setRoleId(UUID.randomUUID().toString());
        role.setName("ROLE_USER");

        Role roleUser = roleRepository.findByName("ROLE_USER").orElse(role);
        user.setRoles(List.of(roleUser));

        //save user
        User savedUser=userRepository.save(user);


        //exclude password from response
         savedUser.setPassword(null);
         
        //convert entity to dto
        return entityToDto(savedUser);
    }



    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setGender(userDto.getGender());
        user.setAbout(userDto.getAbout());
        user.setImageName(userDto.getImageName());

        User updatedUser = userRepository.save(user);

        //exclude password from response
        updatedUser.setPassword(null);

        return entityToDto(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user you are trying to delete does not exist"));

        //delete user image

        try {
            String fullPath = imagePath + user.getImageName();
            Files.delete(Path.of(fullPath));
        }catch (NoSuchFileException e){
            logger.error("File not found");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        userRepository.delete(user);


    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // pageNumber by default starts from 0
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);

        Page<User> page = userRepository.findAll(pageable);

        List<UserDto> userDtos = page.getContent().stream().map(user -> {
            UserDto dto = entityToDto(user);
            dto.setPassword(null);
            return dto;
        }).toList();
        System.out.println(userDtos);

        return Helper.getPageableResponse(page, UserDto.class);

    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID  "+ userId +" not found"));
        user.setPassword(null);
        return entityToDto(user);

    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No user found with this email"));
        user.setPassword(null);
        return entityToDto(user);

    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        Optional<User> users = userRepository.findByNameContaining(keyword);

        return users.stream().map(user -> {
            UserDto dto = entityToDto(user);
            dto.setPassword(null);
            return dto;
        }).toList();
    }


    private User dtoToEntity(UserDto userDto) {
//        return User.builder()
//                .userId(userDto.getUserId())
//                .name(userDto.getName())
//                .email(userDto.getEmail())
//                .password(userDto.getPassword())
//                .gender(userDto.getGender())
//                .about(userDto.getAbout())
//                .url(userDto.getImage_url())
//                .build();

        return mapper.map(userDto,User.class);
    }

    private UserDto entityToDto(User savedUser) {
//        return UserDto.builder()
//                .userId(savedUser.getUserId())
//                .name(savedUser.getName())
//                .email(savedUser.getEmail())
//                .password(savedUser.getPassword())
//                .gender(savedUser.getGender())
//                .about(savedUser.getAbout())
//                .image_url(savedUser.getUrl())
//                .build();

        return mapper.map(savedUser,UserDto.class);
    }

}
