package br.inatel.pos.dm111.vfr.persistance.promo;

public record Promo(String id,
                    String title,
                    String description,
                    String restaurantId,
                    String category,
                    float price) {
}
