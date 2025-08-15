package br.inatel.pos.dm111.vfr.api.restaurant.service;

import br.inatel.pos.dm111.vfr.api.core.ApiException;
import br.inatel.pos.dm111.vfr.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfr.api.restaurant.ProductRequest;
import br.inatel.pos.dm111.vfr.api.restaurant.ProductResponse;
import br.inatel.pos.dm111.vfr.api.restaurant.RestaurantRequest;
import br.inatel.pos.dm111.vfr.api.restaurant.RestaurantResponse;
import br.inatel.pos.dm111.vfr.persistance.promo.Promo;
import br.inatel.pos.dm111.vfr.persistance.promo.PromoRepository;
import br.inatel.pos.dm111.vfr.persistance.restaurant.Product;
import br.inatel.pos.dm111.vfr.persistance.restaurant.Restaurant;
import br.inatel.pos.dm111.vfr.persistance.restaurant.RestaurantRepository;
import br.inatel.pos.dm111.vfr.persistance.user.User;
import br.inatel.pos.dm111.vfr.persistance.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PromoRepository promoRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             UserRepository userRepository,
                             PromoRepository promoRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.promoRepository = promoRepository;
    }


    //#############################################
    //##            PUBLIC METHODS               ##
    //#############################################

    // #### GET ALL RESTAURANTS - SERVICE ####
    public List<RestaurantResponse> searchRestaurants() throws ApiException {
        return retrieveRestaurants()
                .stream()
                .map(this::buildRestaurantResponse)
                .toList();
    }

    // #### GET PROMOS BY RESTAURANT ID - SERVICE ####
    public List<Promo> getPromosByRestaurantId(String restaurantId) throws ApiException {
        return retrievePromoByRestaurantId(restaurantId);
    }

    // #### GET A RESTAURANT BY ID - SERVICE ####
    public RestaurantResponse searchRestaurant(String id) throws ApiException {
        return retrieveRestaurantById(id).map(this::buildRestaurantResponse)
                .orElseThrow(()->{
                    log.warn("Restaurant was not found. Id: {}", id);
                    return new ApiException((AppErrorCode.RESTAURANT_NOT_FOUND));
                });
    }

    // #### CREATE A NEW RESTAURANT - SERVICE ####
    public RestaurantResponse createRestaurant(RestaurantRequest req) throws ApiException {
        validateRestaurantUserType(req);
        var restaurant = buildRestaurant(req);
        restaurantRepository.save(restaurant);
        log.info("Restaurant was successfully created. Id: {}", restaurant.id());
        return buildRestaurantResponse(restaurant);
    }

    // #### UPDATE A RESTAURANT BY ID - SERVICE ####
    public RestaurantResponse updateRestaurant(RestaurantRequest req, String id) throws ApiException {
        var restaurantOpt = retrieveRestaurantById(id);
        if (restaurantOpt.isEmpty()){
            log.warn("Restaurant was not found! Id: {}", id);
            throw  new ApiException(AppErrorCode.RESTAURANT_NOT_FOUND);
        } else {
            validateRestaurantUserType(req);
            var UpdRestaurant = buildRestaurant(req, id);
            restaurantRepository.save(UpdRestaurant);
            log.info("Restaurant was successfully Updated! Id: {}", id);
            return buildRestaurantResponse(UpdRestaurant);
        }
    }

    // #### REMOVE A RESTAURANT BY ID - SERVICE ####
    public void removeRestaurant(String id) throws ApiException {
        var restaurantOpt = retrieveRestaurantById(id);
        if(restaurantOpt.isPresent()) {
            try {
                restaurantRepository.delete(id);
            } catch (ExecutionException | InterruptedException e) {
                log.error("Failed to delete a restaurant from DB.", e);
                throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
            }
        } else {
            log.info("The provided restaurant if was nto fount. Id: {}", id);
        }
    }

    //#############################################
    //##            PRIVATE METHODS              ##
    //#############################################

    // #### BUILD RESTAURANT ####
    private Restaurant buildRestaurant(RestaurantRequest req) {
        var restaurantId = UUID.randomUUID().toString();
        return buildRestaurant(req, restaurantId);
    }

    private Restaurant buildRestaurant(RestaurantRequest req, String restaurantId) {
        var products = req.products().stream()
                .map(this::buildProduct).toList();
        return new Restaurant(restaurantId,
                req.name(),
                req.address(),
                req.userId(),
                req.categories(),
                products);
    }

    // #### BUILD PRODUCT ####
    private Product buildProduct(ProductRequest req) {
        var productId = UUID.randomUUID().toString();
        return new Product( productId,
                req.name(),
                req.description(),
                req.category(),
                req.price()
        );
    }

    // #### BUILD RESPONSES ####
    private RestaurantResponse buildRestaurantResponse(Restaurant restaurant) {
        var products = restaurant.products().stream()
                .map(this::buildProductResponse).toList();

        return new RestaurantResponse(
                restaurant.id(),
                restaurant.name(),
                restaurant.address(),
                restaurant.userId(),
                restaurant.categories(),
                products
        );
    }

    private ProductResponse buildProductResponse(Product product) {
        return new ProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.category(),
                product.price()
        );
    }

    // #### VALIDATE RESTAURANT USER TYPE ####
    private void validateRestaurantUserType(RestaurantRequest req) throws ApiException {
        var userOpt = retrieveUserById(req.userId());
        if (userOpt.isEmpty()){
            log.warn("User was not found! Id: {}", req.userId());
            throw  new ApiException(AppErrorCode.USER_NOT_FOUND);
        } else {
            var user = userOpt.get();
            if(!User.UserType.RESTAURANT.equals(user.type())) {
                log.info("User provided is not valid for this operation. User Id: {}", req.userId());
                throw new ApiException(AppErrorCode.INVALID_USER_TYPE);
            }
        }
    }

    // #### RETRIEVE RESTAURANTS ####
    private List<Restaurant> retrieveRestaurants() throws ApiException {
        try {
            return restaurantRepository.getAll();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read all restaurants from DB.", e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    // #### RETRIEVE RESTAURANT BY ID ####
    private Optional<Restaurant> retrieveRestaurantById(String id) throws ApiException {
        try {
            return restaurantRepository.getById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read a restaurant from DB by id: {}.", id, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    // #### RETRIEVE USER BY ID ####
    private Optional<User> retrieveUserById(String id) throws ApiException {
        try {
            return userRepository.getById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read an users from DB by id: {}.", id, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    // #### RETRIEVE PROMO BY RESTAURANT ID - SERVICE ####
    private List<Promo> retrievePromoByRestaurantId(String restaurantId) throws ApiException {
        try {
            return promoRepository.getByRestaurantId(restaurantId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read a promo from DB by Restaurant id: {}.", restaurantId, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }


}
