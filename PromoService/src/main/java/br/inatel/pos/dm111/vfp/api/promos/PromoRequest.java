package br.inatel.pos.dm111.vfp.api.promos;

import java.util.List;

public record PromoRequest(String title,
                           String description,
                           String restaurantId,
                           String category,
                           float price){
}
