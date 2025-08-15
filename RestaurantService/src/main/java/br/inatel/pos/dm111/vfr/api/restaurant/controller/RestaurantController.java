package br.inatel.pos.dm111.vfr.api.restaurant.controller;

import br.inatel.pos.dm111.vfr.api.core.ApiException;
import br.inatel.pos.dm111.vfr.api.core.AppError;
import br.inatel.pos.dm111.vfr.api.restaurant.RestaurantRequest;
import br.inatel.pos.dm111.vfr.api.restaurant.RestaurantResponse;
import br.inatel.pos.dm111.vfr.api.restaurant.service.RestaurantService;
import br.inatel.pos.dm111.vfr.persistance.promo.Promo;
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
@RequestMapping("/valefood/restaurants")
public class RestaurantController {
    private static final Logger log = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantRequestValidator validator;
    private final RestaurantService service;

    // #### CONSTRUCTOR ####
    public RestaurantController(RestaurantRequestValidator validator, RestaurantService service) {
        this.validator = validator;
        this.service = service;
    }

    // #### GET ALL RESTAURANTS - CONTROLLER ####
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurant() throws ApiException {
        log.debug("Received request to list all restaurants.");
        var response = service.searchRestaurants();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### GET PROMOS BY RESTAURANT ID - CONTROLLER ####
    @GetMapping("/promos/{restaurantId}")
    public List<Promo> getPromos(@PathVariable String restaurantId) throws ApiException {
        return service.getPromosByRestaurantId(restaurantId);
    }


    // #### GET RESTAURANT BY ID - CONTROLLER ####
    @GetMapping(value="/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @PathVariable("restaurantId") String id) throws ApiException {
        log.debug("Received request to list a restaurant by Id: {}", id);
        var response = service.searchRestaurant(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### CREATE A NEW RESTAURANT - CONTROLLER ####
    @PostMapping
    public ResponseEntity<RestaurantResponse> postRestaurant(
            @RequestBody RestaurantRequest request,
            BindingResult bindingResult) throws ApiException {

        log.debug("Received request to create a new restaurant...");

        validateRequest(request, bindingResult);
        var response = service.createRestaurant(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // #### UPDATE A RESTAURANT BY ID - CONTROLLER ####
    @PutMapping(value="/{restaurantId}")
    public ResponseEntity<RestaurantResponse> putRestaurant(
            @RequestBody RestaurantRequest request,
            @PathVariable("restaurantId") String id,
            BindingResult bindingResult) throws ApiException {
        log.debug("Received request to update a restaurant");
        validateRequest(request, bindingResult);
        var response = service.updateRestaurant(request, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### REMOVE A RESTAURANT BY ID - CONTROLLER ####
    @DeleteMapping(value="/{restaurantId}")
    public ResponseEntity<List<RestaurantResponse>> deleteRestaurant(@PathVariable("restaurantId") String id) throws ApiException {
        log.debug("Received request to delete a restaurant. Id: {}", id);
        service.removeRestaurant(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }

    // #### REQUEST VALIDATION - CONTROLLER ####
    private void validateRequest(RestaurantRequest request, BindingResult bindingResult) throws ApiException {
        ValidationUtils.invokeValidator(validator, request, bindingResult);
        if(bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors().stream().map(fe -> new AppError(fe.getCode(), fe.getDefaultMessage())).toList();
            throw new ApiException(HttpStatus.BAD_REQUEST, errors);
        }
    }
}
