package com.kamsan.book.user.application.service;

import com.kamsan.book.config.s3.S3Buckets;
import com.kamsan.book.config.s3.S3Service;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.sharedkernel.utils.Constants;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.UserDTO;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final S3Service s3Service;
    private final S3Buckets buckets;
    private final UserMapper userMapper;


    @Transactional
    public void uploadUserProfileImage(MultipartFile file, UUID userPublicId) {
        var profileImageId = UUID.randomUUID().toString();
        var s3Key = "profile-images/%s/%s".formatted(userPublicId, profileImageId);
        try {
            s3Service.putObject(
                    buckets.getUsers(),
                    s3Key,
                    file.getBytes(),
                    file.getContentType());
        } catch (IOException e) {
            throw new ApiException("Something went wrong when updating user profile image");
        }

        int success = userRepository.updateUserProfileImage(profileImageId, userPublicId);
        if (success != 1) {
            throw new ApiException(String.format("Failed to update profile image of user with public id %s", userPublicId));
        }
    }

    @Transactional(readOnly = true)
    public byte[] getUserProfileImage(UUID userPublicId) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new ApiException(String.format(Constants.PUBLIC_ID_USER_NOT_FOUND_MSG, userPublicId)));
        if (user.getProfileImageId() == null || user.getProfileImageId().isEmpty()){
            throw new ApiException(String.format("Could not retrieve the profile image of user %s", user.getPublicId()));
        }
        return s3Service.getObject(buckets.getUsers(), "profile-images/%s/%s".formatted(user.getPublicId(), user.getProfileImageId()));
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersPage(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::userToUserDTO);
    }
}
