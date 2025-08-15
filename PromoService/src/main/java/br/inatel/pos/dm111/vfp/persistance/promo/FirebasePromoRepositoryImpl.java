package br.inatel.pos.dm111.vfp.persistance.promo;

import br.inatel.pos.dm111.vfp.api.promos.service.PromoService;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Profile("local")
@Component
public class FirebasePromoRepositoryImpl implements PromoRepository
{
    private static final String COLLECTION_NAME = "promos";
    private final Firestore firestore;

    private static final Logger log = LoggerFactory.getLogger(FirebasePromoRepositoryImpl.class);

    public FirebasePromoRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public List<Promo> getAll() throws ExecutionException, InterruptedException {
        return firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments()
                .parallelStream()
                .map(doc -> doc.toObject(Promo.class))
                .toList();
    }

    @Override
    public Optional<Promo> getById(String id) throws ExecutionException, InterruptedException {
        var restaurant = firestore.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .get()
                .toObject(Promo.class);
        return Optional.ofNullable(restaurant);
    }

    @Override
    public List<Promo> getByRestaurantId(String restaurantId) throws ExecutionException, InterruptedException {
        return firestore.collection(COLLECTION_NAME)
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(doc -> doc.toObject(Promo.class))
                .toList();
    }

    @Override
    public List<Promo> getByCategory(String category) throws ExecutionException, InterruptedException {
        return firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(doc -> doc.toObject(Promo.class))
                .filter(promo -> promo.category() != null &&
                        promo.category().equalsIgnoreCase(category))
                .toList();
    }

    @Override
    public List<Promo> getByCategories(List<String> categories) throws ExecutionException, InterruptedException {
        return firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(doc -> doc.toObject(Promo.class))
                .filter(promo -> categories.stream()
                        .anyMatch(cat -> cat.equalsIgnoreCase(promo.category())))
                .toList();
    }

    @Override
    public Promo save(Promo promo) {
        firestore.collection(COLLECTION_NAME)
                .document(promo.id())
                .set(promo);
        return promo;
    }

    @Override
    public void delete(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .get();
    }
}
