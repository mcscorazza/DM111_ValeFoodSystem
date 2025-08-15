package br.inatel.pos.dm111.vfp.api.user.service;

import br.inatel.pos.dm111.vfp.api.user.UserRequest;
import br.inatel.pos.dm111.vfp.persistance.user.User;
import br.inatel.pos.dm111.vfp.persistance.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserRequest createUser(UserRequest req){
        var user = buildUser(req);
        repository.save(user);
        log.info("User was successfully created. Id: {}", user.id());
        return req;
    }

    private User buildUser(UserRequest req) {
        return new User(req.id(), req.name(), req.email(), null, User.UserType.valueOf(req.type()));
    }

}
