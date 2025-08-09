package com.kamsan.book.user.application.dto;

import java.util.UUID;

public record UserDTO(UUID publicId,
                      String firstName,
                      String lastName,
                      String email,
                      String profileImageId,
                      boolean accountLocked,
                      boolean enabled) {

}