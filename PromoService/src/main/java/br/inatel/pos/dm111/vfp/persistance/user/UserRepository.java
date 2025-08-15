package br.inatel.pos.dm111.vfp.persistance.user;

import br.inatel.pos.dm111.vfp.persistance.ValeFoodRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;


public interface UserRepository extends ValeFoodRepository<User> {
    Optional<User> getByEmail(String email) throws ExecutionException, InterruptedException;
}
