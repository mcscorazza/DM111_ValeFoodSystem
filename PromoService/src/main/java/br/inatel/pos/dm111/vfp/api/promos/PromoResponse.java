package br.inatel.pos.dm111.vfp.api.promos;

public record PromoResponse(String id,
                            String title,
                            String description,
                            String restaurantId,
                            String category,
                            float price) {
}
