package br.inatel.pos.dm111.vfu.persistance.promo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Profile("test")
@Component
public class MemoryPromoRepositoryImpl implements PromoRepository {

    private final Map<String, Promo> db = new HashMap<>();

    @Override
    public List<Promo> getAll() {
        return db.values().stream().toList();
    }

    @Override
    public Optional<Promo> getById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public List<Promo> getByRestaurantId(String restaurantId) throws ExecutionException, InterruptedException {
        return db.values().stream().toList();
    }

    @Override
    public List<Promo> getByCategory(String category) throws ExecutionException, InterruptedException {
        return db.values().stream().toList();
    }

    @Override
    public List<Promo> getByCategories(List<String> category) throws ExecutionException, InterruptedException {
        return db.values().stream().toList();
    }

    @Override
    public Promo save(Promo promo) {
        return db.put(promo.id(), promo);
    }

    @Override
    public void delete(String id) {
        db.values().removeIf(restaurant -> restaurant.id().equals(id));
    }
}
