package br.inatel.pos.dm111.vfr.persistance.promo;

import br.inatel.pos.dm111.vfr.persistance.ValeFoodRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PromoRepository extends ValeFoodRepository<Promo> {
    List<Promo> getByRestaurantId(String restaurantId) throws ExecutionException, InterruptedException;
    List<Promo> getByCategories(List<String> category) throws ExecutionException, InterruptedException;
    List<Promo> getByCategory(String category) throws ExecutionException, InterruptedException;
}
