package com.kamsan.book.user;

import com.kamsan.book.config.s3.S3Buckets;
import com.kamsan.book.config.s3.S3Service;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.sharedkernel.utils.Constants;
import com.kamsan.book.user.application.service.UserService;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private S3Buckets buckets;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserRepository userRepository;


    @Test
    void canUploadProfileImage() {
        // Given
        UUID userPublicId = UUID.randomUUID();
        byte[] bytes = "Hello World".getBytes();

        MultipartFile multipartFile = new MockMultipartFile("file", "file.txt", "text/plain", bytes);
        String bucket = "user-bucket";

        when(buckets.getUsers()).thenReturn(bucket);

        // Mock userRepository to return success (1)
        when(userRepository.updateUserProfileImage(anyString(), eq(userPublicId))).thenReturn(1);
        // When
        userService.uploadUserProfileImage(multipartFile, userPublicId);

        // Then
        ArgumentCaptor<String> profileImageIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).updateUserProfileImage(profileImageIdCaptor.capture(), eq(userPublicId));
        String expectedKey = "profile-images/%s/%s".formatted(userPublicId, profileImageIdCaptor.getValue());
        verify(s3Service).putObject(bucket, expectedKey, bytes, "text/plain");
    }

    @Test
    void cannotUploadProfileImageWhenIOExceptionIsThrown() throws IOException {
        // Given
        UUID userPublicId = UUID.randomUUID();
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(multipartFile.getBytes()).thenThrow(new IOException("Simulated IO Error"));
        when(buckets.getUsers()).thenReturn("user-bucket");

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.uploadUserProfileImage(multipartFile, userPublicId);
        });
        assertEquals("Something went wrong when updating user profile image", exception.getMessage());
        verifyNoInteractions(s3Service, userRepository);
    }

    @Test
    void cannotUploadProfileImageWhenUpdateFails() throws IOException {
        // Given
        UUID userPublicId = UUID.randomUUID();
        byte[] bytes = "image data".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", bytes);

        String bucket = "customer-bucket";
        when(buckets.getUsers()).thenReturn(bucket);
        when(userRepository.updateUserProfileImage(anyString(), eq(userPublicId))).thenReturn(0); // simulate failure

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.uploadUserProfileImage(multipartFile, userPublicId);
        });

        assertTrue(exception.getMessage().contains("Failed to update profile image of user with public id"));

        // Verify S3 upload still happened before the failure
        verify(s3Service).putObject(anyString(), anyString(), eq(bytes), eq("image/jpeg"));
    }

    @Test
    void canDownloadProfileImage() {
        UUID userPublicId = UUID.randomUUID();
        String profileImageId = "2222";
        User user = User.builder()
                .profileImageId(profileImageId)
                .publicId(userPublicId)
                .build();
        when(userRepository.findByPublicId(userPublicId)).thenReturn(Optional.of(user));
        String bucket = "customer-bucket";
        when(buckets.getUsers()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();
        when(s3Service.getObject(buckets.getUsers(), "profile-images/%s/%s".formatted(user.getPublicId(), user.getProfileImageId())))
                .thenReturn(expectedImage);
        // When
        byte[] actualImage = userService.getUserProfileImage(userPublicId);
        // Then
        assertEquals(actualImage, expectedImage);
    }

    @Test
    void cannotDownloadWhenNoProfileImageId() {
        UUID userPublicId = UUID.randomUUID();
        User user = User.builder()
                .profileImageId(null)
                .publicId(userPublicId)
                .build();
        when(userRepository.findByPublicId(userPublicId)).thenReturn(Optional.of(user));

        // When
        // Then
        assertThatThrownBy(() -> userService.getUserProfileImage(userPublicId))
                .isInstanceOf(ApiException.class)
                .hasMessage("Could not retrieve the profile image of user %s".formatted(user.getPublicId()));

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.getUserProfileImage(userPublicId);
        });

        verifyNoInteractions(buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotDownloadProfileImageWhenUserDoesNotExist() {
        UUID userPublicId = UUID.randomUUID();
        when(userRepository.findByPublicId(userPublicId)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> userService.getUserProfileImage(userPublicId))
                .isInstanceOf(ApiException.class)
                .hasMessage(String.format(Constants.USER_NOT_FOUND_MSG, userPublicId));
        verifyNoInteractions(buckets);
        verifyNoInteractions(s3Service);
    }


}
