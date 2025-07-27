package com.kamsan.book.user.presentation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("management")
@Tag(name = "Management")
@PreAuthorize("hasRole('MANAGER')")
public class ManagementResource {


//    @Operation(
//            description = "Get endpoint for manager",
//            summary = "This is a summary for management get endpoint",
//            responses = {
//                    @ApiResponse(
//                            description = "Success",
//                            responseCode = "200"
//                    ),
//                    @ApiResponse(
//                            description = "Unauthorized / Invalid Token",
//                            responseCode = "403"
//                    )
//            }
//
//    )
    @GetMapping
    @PreAuthorize("hasAuthority('management:read')")
    public String get() {
        return "GET:: management controller";
    }
    @PostMapping
    @PreAuthorize("hasAuthority('management:create')")
    public String post() {
        return "POST:: management controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority('management:update')")
    public String put() {
        return "PUT:: management controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('management:delete')")
    public String delete() {
        return "DELETE:: management controller";
    }
}