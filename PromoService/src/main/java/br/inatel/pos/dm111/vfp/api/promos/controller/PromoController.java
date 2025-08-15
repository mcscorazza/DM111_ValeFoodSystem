package br.inatel.pos.dm111.vfp.api.promos.controller;

import br.inatel.pos.dm111.vfp.api.core.ApiException;
import br.inatel.pos.dm111.vfp.api.core.AppError;
import br.inatel.pos.dm111.vfp.api.promos.PromoRequest;
import br.inatel.pos.dm111.vfp.api.promos.PromoResponse;
import br.inatel.pos.dm111.vfp.api.promos.service.PromoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/valefood/promos")
public class PromoController {
    private static final Logger log = LoggerFactory.getLogger(PromoController.class);

    private final PromoRequestValidator validator;
    private final PromoService service;

    public PromoController(PromoRequestValidator validator, PromoService service) {
        this.validator = validator;
        this.service = service;
    }

    // #### READ ALL PROMOS - CONTROLLER ####
    @GetMapping
    public ResponseEntity<List<PromoResponse>> getAllPromo() throws ApiException {
        log.debug("Received request to list all promos.");
        var response = service.searchPromos();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### READ PROMO BY ID - CONTROLLER ####
    @GetMapping(value="/{promoId}")
    public ResponseEntity<PromoResponse> getPromoById(
            @PathVariable("promoId") String id) throws ApiException {
        log.debug("Received request to list a promo by Id: {}", id);
        var response = service.searchPromo(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
    // #### READ PROMO BY CATEGORY - CONTROLLER ####
    @GetMapping(value="/category/{category}")
    public ResponseEntity<List<PromoResponse>> getPromoByCategory(
            @PathVariable("category") String category) throws ApiException {
        log.debug("Received request to list a promo by Category: {}", category);
        var response = service.searchPromoByCategory(category);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### READ PROMO BY RESTAURANT ID - CONTROLLER ####
    @GetMapping(value="/restaurant/{restaurantId}")
    public ResponseEntity<List<PromoResponse>> getPromoByRestaurantId(
            @PathVariable("restaurantId") String restaurantId) throws ApiException {
        log.debug("Received request to list a promo by Restaurant Id: {}", restaurantId);
        var response = service.searchPromoByRestaurant(restaurantId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
    // #### CREATE A NEW PROMO - CONTROLLER ####
    @PostMapping
    public ResponseEntity<PromoResponse> postPromo(
            @RequestBody PromoRequest request,
            BindingResult bindingResult) throws ApiException {
        log.debug("Received request to create a new promo...");
        validateRequest(request, bindingResult);
        var response = service.createPromo(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // #### UPDATE A PROMO BY ID - CONTROLLER ####
    @PutMapping(value="/{promoId}")
    public ResponseEntity<PromoResponse> putPromo(
            @RequestBody PromoRequest request,
            @PathVariable("promoId") String id,
            BindingResult bindingResult) throws ApiException {
        log.debug("Received request to update a promo");

        validateRequest(request, bindingResult);

        var response = service.updatePromo(request, id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // #### DELETE A PROMO BY ID - CONTROLLER ####
    @DeleteMapping(value="/{promoId}")
    public ResponseEntity<List<PromoResponse>> removePromo(@PathVariable("promoId") String id) throws ApiException {
        log.debug("Received request to delete a promo. Id: {}", id);
        service.removePromo(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }

    private void validateRequest(PromoRequest request, BindingResult bindingResult) throws ApiException {
        ValidationUtils.invokeValidator(validator, request, bindingResult);
        if(bindingResult.hasErrors()) {
            var errors = bindingResult.getFieldErrors().stream().map(fe -> new AppError(fe.getCode(), fe.getDefaultMessage())).toList();
            throw new ApiException(HttpStatus.BAD_REQUEST, errors);
        }
    }
}
