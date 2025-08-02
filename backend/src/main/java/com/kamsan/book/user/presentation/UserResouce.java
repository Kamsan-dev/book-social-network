package com.kamsan.book.user.presentation;

import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.service.AuthenticationService;
import com.kamsan.book.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserResouce {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping(value = "profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserProfileImage(
            @RequestParam(name = "file") MultipartFile file) {
        ReadUserDTO connectedUser = authenticationService.getAuthenticatedUserFromSecurityContext();
        userService.uploadUserProfileImage(file, connectedUser.publicId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "users/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUsersProfileImage(@PathVariable(name = "userId") UUID userPublicId,
                                                     @RequestParam(name = "file") MultipartFile file) {
        userService.uploadUserProfileImage(file, userPublicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "{userId}/profile-image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable(name = "userId") UUID userPublicId) {
        return new ResponseEntity<>(userService.getUserProfileImage(userPublicId), HttpStatus.OK);
    }
}
