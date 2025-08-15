package br.inatel.pos.dm111.vfu.persistance.user;

import br.inatel.pos.dm111.vfu.persistance.ValeFoodRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;


public interface UserRepository extends ValeFoodRepository<User> {
    Optional<User> getByEmail(String email) throws ExecutionException, InterruptedException;
}
