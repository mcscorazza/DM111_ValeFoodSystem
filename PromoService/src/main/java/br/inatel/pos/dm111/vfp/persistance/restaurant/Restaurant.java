package br.inatel.pos.dm111.vfp.persistance.restaurant;

import java.util.List;

public record Restaurant(String id,
                         String name,
                         String address,
                         String userId,
                         List<String> categories,
                         List<Product> products) {
}
