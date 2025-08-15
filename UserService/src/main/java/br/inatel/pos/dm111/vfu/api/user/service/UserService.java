package br.inatel.pos.dm111.vfu.api.user.service;

import br.inatel.pos.dm111.vfu.api.PasswordEncryptor;
import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfu.api.user.UserRequest;
import br.inatel.pos.dm111.vfu.api.user.UserResponse;
import br.inatel.pos.dm111.vfu.persistance.promo.Promo;
import br.inatel.pos.dm111.vfu.persistance.promo.PromoRepository;
import br.inatel.pos.dm111.vfu.persistance.user.User;
import br.inatel.pos.dm111.vfu.persistance.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PromoRepository promoRepository;
    private final PasswordEncryptor encryptor;

    public UserService(UserRepository repository, PromoRepository promoRepository, PasswordEncryptor encryptor)
    {
        this.userRepository = repository;
        this.promoRepository = promoRepository;
        this.encryptor = encryptor;
    }

    //#############################################
    //##                PUBLIC                   ##
    //#############################################

    // #### READ ALL USERS - SERVICE ####
    public List<UserResponse> searchUsers() throws ApiException {
        return retrieveUsers()
                .stream()
                .map(this::buildUserResponse)
                .toList();
    }
    // #### READ AN USER BY ID - SERVICE ####
    public UserResponse searchUser(String id) throws ApiException {
        return  retrieveUserById(id)
                .map(this::buildUserResponse)
                .orElseThrow(()->{
                    log.warn("User was not found. Id: {}", id);
                    return new ApiException((AppErrorCode.USER_NOT_FOUND));
                });
    }
    // #### READ PROMOS BY USER FAVORITE CATEGORIES - SERVICE ####
    public List<Promo> getPromosByUser(String userId) throws ApiException {
        var userCategories = retrieveUserCategories(userId);
        log.info("User Categories: {}", userCategories);
        return retrievePromosByCategories(userCategories);
    }

    // #### CREATE A NEW USER - SERVICE ####
    public UserResponse createUser(UserRequest req) throws ApiException {
        validateUser(req);
        var user = buildUser(req);
        userRepository.save(user);
        log.info("User was successfully created. Id: {}", user.id());
        return buildUserResponse(user);
    }

    // #### UPDATE AN USER BY ID - SERVICE ####
    public UserResponse updateUser(UserRequest req, String id) throws ApiException {
        // check user by id exist
        var userOpt = retrieveUserById(id);
        if (userOpt.isEmpty()){
            log.warn("User was not found! Id: {}", id);
            throw  new ApiException(AppErrorCode.USER_NOT_FOUND);
        } else {
            var user = userOpt.get();
            if(req.email() != null && !user.email().equals(req.email())) {
                validateUser(req);
            }
        }
        var user = buildUser(req, id);
        userRepository.save(user);
        log.info("User was successfully Updated! Id: {}", id);

        return buildUserResponse(user);
    }

    // #### DELETE AN USER BY ID - SERVICE ####
    public void removeUser(String id) throws ApiException {
        try {
            userRepository.delete(id);
            log.info("User was successfully deleted! Id: {}", id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to delete an users from DB by Id: {}.", id, e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }

    //#############################################
    //##                PRIVATE                  ##
    //#############################################

    // #### BUILDS ####
    private UserResponse buildUserResponse(User user) {
        return new UserResponse(user.id(), user.name(), user.email(), user.type().name(), user.categories());
    }

    private User buildUser(UserRequest req) throws ApiException {
        var encryptedPwd = encryptor.encrypt(req.password());
        var userId = UUID.randomUUID().toString();
        List<String> categories = null;
        if ("REGULAR".equals(req.type())) {
            categories = req.categories();
        } else {
            categories = List.of();
        }
        return new User(userId, req.name(), req.email(), encryptedPwd, User.UserType.valueOf(req.type()), categories);
    }

    private User buildUser(UserRequest req, String id) {
        var encryptedPwd = encryptor.encrypt(req.password());
        return new User(id, req.name(), req.email(), encryptedPwd, User.UserType.valueOf(req.type()), req.categories());
    }

    // #### VALIDATE USER ####
    private void validateUser(UserRequest req) throws ApiException {
        var userOpt = retrieveUserByEmail(req.email());
        if (userOpt.isPresent()) {
            log.warn("Provided email already in use.");
            throw new ApiException(AppErrorCode.CONFLICTED_USER_EMAIL);
        }
    }

    // #### RETRIEVES ####
    private List<User> retrieveUsers() throws ApiException {
        try {
            return userRepository.getAll();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read all users from DB.", e);
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
    private Optional<User> retrieveUserByEmail(String email) throws ApiException {
        try {
            return userRepository.getByEmail(email);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read an users from DB by email.", e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }
    private  List<String> retrieveUserCategories(String userId) throws ApiException {
        return retrieveUserById(userId)
                .map(user -> Optional.ofNullable(user.categories()).orElse(emptyList()))
                .orElseThrow(() -> {
                    log.warn("User was not found. Id: {}", userId);
                    return new ApiException(AppErrorCode.USER_NOT_FOUND);
                });
    }

    private List<Promo> retrievePromosByCategory(String category) throws ApiException {
        try {
            return promoRepository.getByCategory(category);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read a Promo from DB by category.", e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }
    private List<Promo> retrievePromosByCategories(List<String> category) throws ApiException {
        try {
            return promoRepository.getByCategories(category);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to read a Promo from DB by category.", e);
            throw new ApiException(AppErrorCode.INTERNAL_DB_COMMUNICATION_ERROR);
        }
    }
}
