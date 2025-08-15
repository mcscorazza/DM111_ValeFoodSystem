package br.inatel.pos.dm111.vfp.api.promos.service;

import br.inatel.pos.dm111.vfp.api.core.ApiException;
import br.inatel.pos.dm111.vfp.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfp.api.promos.PromoRequest;
import br.inatel.pos.dm111.vfp.api.promos.PromoResponse;
import br.inatel.pos.dm111.vfp.persistance.promo.Promo;
import br.inatel.pos.dm111.vfp.persistance.promo.PromoRepository;
import br.inatel.pos.dm111.vfp.persistance.restaurant.Restaurant;
import br.inatel.pos.dm111.vfp.persistance.restaurant.RestaurantRepository;
import br.inatel.pos.dm111.vfp.persistance.user.User;
import br.inatel.pos.dm111.vfp.persistance.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class PromoService {

    private static final Logger log = LoggerFactory.getLogger(PromoService.class);
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PromoRepository promoRepository;

    public PromoService(RestaurantRepository restaurantRepository, UserRepository userRepository, PromoRepository promoRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.promoRepository = promoRepository;
    }


    //#############################################
    //##            PUBLIC METHODS               ##
    //#############################################

    // #### READ ALL PROMOS - SERVICE ####
    public List<PromoResponse> searchPromos() throws ApiException {
        return retrievePromos()
                .stream()
                .map(this::buildPromoResponse)
                .toList();
    }
    // #### READ PROMO BY ID - SERVICE ####
    public PromoResponse searchPromo(String id) throws ApiException {
        return retrievePromoById(id).map(this::buildPromoResponse)
                .orElseThrow(()->{
                    log.warn("Promo was not found. Id: {}", id);
                    return new ApiException((AppErrorCode.RESTAURANT_NOT_FOUND));
                });
    }
    // #### READ PROMO BY CATEGORY - SERVICE ####
    public List<PromoResponse> searchPromoByCategory(String category) throws ApiException {
        List<Promo> promos = retrievePromosByCategory(category);
        if (promos.isEmpty()) {
            log.warn("Promo was not found. Category: {}", category);
            throw new ApiException(AppErrorCode.RESTAURANT_NOT_FOUND);
        }
        return promos.stream()
                .map(this::buildPromoResponse)
                .toList();
    }

    // #### READ PROMO BY RESTAURANT ID - SERVICE ####
    public List<PromoResponse> searchPromoByRestaurant(String restaurantId) throws ApiException {
        List<Promo> promos = retrievePromoByRestaurantId(restaurantId);
        if (promos.isEmpty()) {
            log.warn("Promo was not found. RestaurantId: {}", restaurantId);
            throw new ApiException(AppErrorCode.RESTAURANT_NOT_FOUND);
        }
        return promos.stream()
                .map(this::buildPromoResponse)
                .toList();
    }

    // #### CREATE PROMO - SERVICE ####
    public PromoResponse createPromo(PromoRequest req) throws ApiException {
        validatePromoUserType(req);
        var promo = buildPromo(req);
        promoRepository.save(promo);
        log.info("Promo was successfully created. Id: {}", promo.id());
        return buildPromoResponse(promo);
    }
    // #### UPDATE PROMO - SERVICE ####
    public PromoResponse updatePromo(PromoRequest req, String id) throws ApiException {
        var promoOpt = retrievePromoById(id);
        if (promoOpt.isEmpty()){
            log.warn("Promo was not found! Id: {}", id);
            throw  new ApiException(AppErrorCode.PROMO_NOT_FOUND);
        } else {
            validatePromoUserType(req);
            var UpdPromo = buildPromo(req, id);
            promoRepository.save(UpdPromo);
            log.info("Promo was successfully Updated! Id: {}", id);
            return buildPromoResponse(UpdPromo);
        }
    }
    // #### DELETE PROMO - SERVICE ####
    public void removePromo(String id) throws ApiException {
        var promoOpt = retrievePromoById(id);
        if(promoOpt.isPresent()) {
            try {
                promoRepository.delete(id);
            } catch (ExecutionException | InterruptedException e) {
                log.error("Failed to delete a promo from DB.", e);
                throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
            }
        } else {
            log.info("The provided promo id was not fount. Id: {}", id);
        }
    }

    //#############################################
    //##            PRIVATE METHODS              ##
    //#############################################

    // #### BUILD PROMO ####
    private Promo buildPromo(PromoRequest req) {
        var promoId = UUID.randomUUID().toString();
        return buildPromo(req, promoId);
    }

    private Promo buildPromo(PromoRequest req, String promoId) {
        return new Promo(promoId,
                req.title(),
                req.description(),
                req.restaurantId(),
                req.category(),
                req.price());
    }

    // #### BUILD RESPONSE ####
    private PromoResponse buildPromoResponse(Promo promo) {
        return new PromoResponse(
                promo.id(),
                promo.title(),
                promo.description(),
                promo.restaurantId(),
                promo.category(),
                promo.price()
        );
    }

    // #### VALIDATE PROMO USER TYPE ####
    private void validatePromoUserType(PromoRequest req) throws ApiException {
        var restaurantOpt = retrieveRestaurantById(req.restaurantId());
        if (restaurantOpt.isEmpty()){
            log.warn("Restaurant was not found! Id: {}", req.restaurantId());
            throw  new ApiException(AppErrorCode.RESTAURANT_NOT_FOUND);
        } else {
            var restaurant = restaurantOpt.get();
            var userOpt = retrieveUserById(restaurant.userId());
            if (userOpt.isEmpty()) {
                log.warn("User was not found! Id: {}", restaurant.userId());
                throw  new ApiException(AppErrorCode.USER_NOT_FOUND);
            } else {
                var user = userOpt.get();
                if(!User.UserType.RESTAURANT.equals(user.type())) {
                    log.info("User provided is not valid for this operation. User Id: {}", req.restaurantId());
                    throw new ApiException(AppErrorCode.INVALID_USER_TYPE);
                }
            }
        }
    }

    // #### RETRIEVES ####
    private List<Promo> retrievePromos() throws ApiException {
        try {
            return promoRepository.getAll();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read all restaurants from DB.", e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    private Optional<Restaurant> retrieveRestaurantById(String id) throws ApiException {
        try {
            return restaurantRepository.getById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read a restaurant from DB by id: {}.", id, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    private Optional<User> retrieveUserById(String id) throws ApiException {
        try {
            return userRepository.getById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read an users from DB by id: {}.", id, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    private Optional<Promo> retrievePromoById(String id) throws ApiException {
        try {
            return promoRepository.getById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read a promo from DB by id: {}.", id, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    private List<Promo> retrievePromoByRestaurantId(String restaurantId) throws ApiException {
        try {
            return promoRepository.getByRestaurantId(restaurantId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read a promo from DB by Restaurant id: {}.", restaurantId, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    private List<Promo> retrievePromosByCategories(List<String> category) throws ApiException {
        try {
            return promoRepository.getByCategories(category);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read an users from DB by email.", e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    private List<Promo> retrievePromosByCategory(String category) throws ApiException {
        try {
            return promoRepository.getByCategory(category);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read an users from DB by email.", e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

}
