package com.kamsan.book.user.application.service;

import com.kamsan.book.config.s3.S3Buckets;
import com.kamsan.book.config.s3.S3Service;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.sharedkernel.utils.Constants;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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


    @Transactional
    public String uploadUserProfileImage(MultipartFile file, UUID userPublicId) {
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

        // Construct the S3 URL
        return String.format("https://%s.s3.eu-north-1.amazonaws.com/%s", buckets.getUsers(), s3Key);
    }

    @Transactional(readOnly = true)
    public byte[] getUserProfileImage(UUID userPublicId) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new ApiException(String.format(Constants.USER_NOT_FOUND_MSG, userPublicId)));
        if (user.getProfileImageId().isEmpty() || user.getProfileImageId() == null){
            new ApiException(String.format("Could not retrieve the profile image of user %s", user.getPublicId()));
        }
        return s3Service.getObject(buckets.getUsers(), "profile-images/%s/%s".formatted(user.getPublicId(), user.getProfileImageId()));
    }
}
