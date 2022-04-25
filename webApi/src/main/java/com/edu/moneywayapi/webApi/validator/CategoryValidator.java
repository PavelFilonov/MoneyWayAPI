package com.edu.moneywayapi.webApi.validator;

import br.com.fluentvalidator.AbstractValidator;
import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static br.com.fluentvalidator.predicate.LogicalPredicate.not;
import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;

@Component
public class CategoryValidator extends AbstractValidator<CategoryDTO> {

    @Value("${category.name.empty}")
    private String isNameEmptyMessage;

    @Override
    public void rules() {
        ruleFor(CategoryDTO::getName)
                .must(not(stringEmptyOrNull()))
                .withMessage(isNameEmptyMessage)
                .withFieldName("name")
                .critical();
    }
}
