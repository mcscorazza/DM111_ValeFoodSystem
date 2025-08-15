package br.inatel.pos.dm111.vfp.api.promos.controller;

import br.inatel.pos.dm111.vfp.api.promos.PromoRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class PromoRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PromoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title.empty", "Title is required!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "description.empty", "Description is required!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "restaurantId", "restaurantId.empty", "Restaurant ID is required!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "category", "category.empty", "Category is required!");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "price.empty", "Price is required!");
    }
}
