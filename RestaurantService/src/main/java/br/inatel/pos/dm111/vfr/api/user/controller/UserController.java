package br.inatel.pos.dm111.vfr.api.user.controller;

import br.inatel.pos.dm111.vfr.api.user.UserRequest;
import br.inatel.pos.dm111.vfr.api.core.ApiException;
import br.inatel.pos.dm111.vfr.api.core.AppError;
import br.inatel.pos.dm111.vfr.api.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/valefood/users")
public class UserController {

    private final UserService service;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserRequest> postUser(@RequestBody UserRequest request) {
        log.debug("Received request to create a new user...");

        var response = service.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
