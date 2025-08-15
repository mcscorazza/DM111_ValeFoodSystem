package br.inatel.pos.dm111.vfr.persistance.restaurant;

import br.inatel.pos.dm111.vfr.persistance.ValeFoodRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface RestaurantRepository extends ValeFoodRepository<Restaurant> {
    Optional<Restaurant> getByUserId(String userId) throws ExecutionException, InterruptedException;
}