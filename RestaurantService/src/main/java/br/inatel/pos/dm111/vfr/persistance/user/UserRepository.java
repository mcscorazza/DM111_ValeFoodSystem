package br.inatel.pos.dm111.vfr.persistance.user;

import br.inatel.pos.dm111.vfr.persistance.ValeFoodRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;


public interface UserRepository extends ValeFoodRepository<User> {
    Optional<User> getByEmail(String email) throws ExecutionException, InterruptedException;
}
