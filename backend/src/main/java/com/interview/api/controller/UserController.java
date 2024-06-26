package com.interview.api.controller;

import com.interview.api.dto.UserDTO;
import com.interview.api.service.UserService;
import com.interview.api.utils.CreateOperation;
import com.interview.api.utils.UpdateOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    public ResponseEntity<UserDTO> createUser(@Validated(CreateOperation.class) @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get a user by username", description = "Retrieves a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getUser(@PathVariable("username") @NotBlank @Parameter(description = "Username of the user") String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public Page<UserDTO> getUsers(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortDirection) {
        return userService.getUsers(page, size, sortBy, sortDirection);
    }

    @PutMapping
    @Operation(summary = "Update a user", description = "Updates an existing user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> updateUser(@Validated(UpdateOperation.class) @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok().body(userService.updateUser(userDTO));
    }


    @Operation(summary = "Delete a user", description = "Deletes a user by their id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") @NotBlank @Parameter(description = "Id of the user") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("User successfully deleted ");
    }
}