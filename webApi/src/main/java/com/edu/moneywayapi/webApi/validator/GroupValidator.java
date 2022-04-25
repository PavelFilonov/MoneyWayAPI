package com.edu.moneywayapi.webApi.validator;

import br.com.fluentvalidator.AbstractValidator;
import br.com.fluentvalidator.context.Error;
import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.webApi.dto.GroupDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static br.com.fluentvalidator.predicate.LogicalPredicate.not;
import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;

@Component
public class GroupValidator extends AbstractValidator<GroupDTO> {

    @Value("${group.owner-id.null}")
    private String isOwnerIdNullMessage;

    @Value("${group.owner-id.not-positive}")
    private String isOwnerIdNotPositiveMessage;

    @Value("${group.name.empty}")
    private String isNameEmptyMessage;

    @Value("${group.token.empty}")
    private String isTokenEmptyMessage;

    @Override
    public ValidationResult validate(GroupDTO instance) {
        ValidationResult result = super.validate(instance);
        if (!result.isValid())
            return result;

        Collection<Error> errors = new ArrayList<>();

        if (instance.getOwnerId() == null)
            errors.add(Error.create("ownerId", isOwnerIdNullMessage, null, null));
        else if (instance.getOwnerId() <= 0)
            errors.add(Error.create("ownerId", isOwnerIdNotPositiveMessage, null, null));

        if (errors.isEmpty())
            return ValidationResult.ok();

        return ValidationResult.fail(errors);
    }

    @Override
    public void rules() {
        ruleFor(GroupDTO::getName)
                .must(not(stringEmptyOrNull()))
                .withMessage(isNameEmptyMessage)
                .withFieldName("name")
                .critical();

        ruleFor(GroupDTO::getToken)
                .must(not(stringEmptyOrNull()))
                .withMessage(isTokenEmptyMessage)
                .withFieldName("token")
                .critical();
    }
}
