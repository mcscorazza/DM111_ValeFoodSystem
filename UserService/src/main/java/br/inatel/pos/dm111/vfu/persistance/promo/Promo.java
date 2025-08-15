package br.inatel.pos.dm111.vfu.persistance.promo;

public record Promo(String id,
                    String title,
                    String description,
                    String restaurantId,
                    String category,
                    float price) {
}
