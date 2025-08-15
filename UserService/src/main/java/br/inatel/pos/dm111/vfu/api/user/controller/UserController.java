package br.inatel.pos.dm111.vfu.api.user.controller;

import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.core.AppError;
import br.inatel.pos.dm111.vfu.api.user.UserRequest;
import br.inatel.pos.dm111.vfu.api.user.UserResponse;
import br.inatel.pos.dm111.vfu.api.user.service.UserService;
import br.inatel.pos.dm111.vfu.persistance.promo.Promo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/valefood/users")
public class UserController {
    private final UserRequestValidator validator;
    private final UserService service;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public UserController(UserRequestValidator validator, UserService service) {
        this.validator = validator;
        this.service = service;
    }

    // #### READ ALL USERS - CONTROLLER ####
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() throws ApiException {
        log.debug("Received request to list all users.");
        var response = service.searchUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### READ AN USER BY ID - CONTROLLER ####
    @GetMapping(value="/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable("userId") String id) throws ApiException {
        log.debug("Received request to list an user by Id: {}", id);
        var response = service.searchUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### READ PROMOS BY USER FAVORITE CATEGORIES - CONTROLLER ####
    @GetMapping("/promos/{userId}")
    public List<Promo> getPromos(@PathVariable String userId) throws ExecutionException, InterruptedException, ApiException {
        log.debug("Received request to list promos for an user by Id: {}", userId);
        var response = service.getPromosByUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response).getBody();
    }

    // #### CREATE A NEW USER - CONTROLLER ####
    @PostMapping
    public ResponseEntity<UserResponse> postUser(
            @RequestBody UserRequest request,
            BindingResult bindingResult) throws ApiException {
        log.debug("Received request to create a new user...");

        validateUserRequest(request, bindingResult);
        var response = service.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // #### UPDATE AN USER BY ID - CONTROLLER ####
    @PutMapping(value="/{userId}")
    public ResponseEntity<UserResponse> putUser(
            @RequestBody UserRequest request,
            @PathVariable("userId") String userId,
            BindingResult bindingResult) throws ApiException {
        log.debug("Received request to update an user");

        validateUserRequest(request, bindingResult);

        var response = service.updateUser(request, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### DELETE AN USER BY ID - CONTROLLER ####
    @DeleteMapping(value="/{userId}")
    public ResponseEntity<List<UserResponse>> deleteUser(@PathVariable("userId") String id) throws ApiException {
        log.debug("Received request to delete an user. Id: {}", id);
        service.removeUser(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }

    // #### REQUEST VALIDATION - CONTROLLER ####
    private void validateUserRequest(UserRequest request, BindingResult bindingResult) throws ApiException {
        ValidationUtils.invokeValidator(validator, request, bindingResult);
        if(bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors().stream().map(fe -> new AppError(fe.getCode(), fe.getDefaultMessage())).toList();
            throw new ApiException(HttpStatus.BAD_REQUEST, errors);
        }
    }
}
