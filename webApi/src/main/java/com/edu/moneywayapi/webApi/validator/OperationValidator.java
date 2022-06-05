package com.edu.moneywayapi.webApi.validator;

import br.com.fluentvalidator.AbstractValidator;
import br.com.fluentvalidator.context.Error;
import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.webApi.dto.OperationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static br.com.fluentvalidator.predicate.LogicalPredicate.not;
import static br.com.fluentvalidator.predicate.ObjectPredicate.nullValue;
import static br.com.fluentvalidator.predicate.StringPredicate.isDateTime;
import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;

@Component
public class OperationValidator extends AbstractValidator<OperationDTO> {

    @Autowired
    private CategoryValidator categoryValidator;

    @Value("${operation.type.null}")
    private String isTypeOperationNullMessage;

    @Value("${operation.category.null}")
    private String isCategoryNullMessage;

    @Value("${operation.value.empty}")
    private String isValueEmptyMessage;

    @Value("${operation.date.empty}")
    private String isDateOperationEmptyMessage;

    @Value("${format.date-time}")
    private String formatDateTime;

    @Value("${operation.date.not-date-time}")
    private String isDateOperationNotDateTimeMessage;

    @Override
    public ValidationResult validate(OperationDTO instance) {
        ValidationResult result = super.validate(instance);
        if (!result.isValid())
            return result;

        Collection<Error> errors = new ArrayList<>();

        if (instance.getType() == null)
            errors.add(Error.create("typeOperation", isTypeOperationNullMessage, null, null));

        if (instance.getCategory() == null)
            errors.add(Error.create("category", isCategoryNullMessage, null, null));
        else {
            ValidationResult categoryValidationResult = categoryValidator.validate(instance.getCategory());
            if (!categoryValidationResult.isValid())
                errors.addAll(categoryValidationResult.getErrors());
        }

        if (errors.isEmpty())
            return ValidationResult.ok();

        return ValidationResult.fail(errors);
    }

    @Override
    public void rules() {
        ruleFor(OperationDTO::getValue)
                .must(not(nullValue()))
                .withMessage(isValueEmptyMessage)
                .withFieldName("value")
                .critical();

//        ruleFor(OperationDTO::getCreatedAt)
//                .must(not(stringEmptyOrNull()))
//                .withMessage(isDateOperationEmptyMessage)
//                .withFieldName("dateOperation")
//                .critical();

//        ruleFor(OperationDTO::getCreatedAt)
//                .must(not(isDateTime(formatDateTime)))
//                .withMessage(isDateOperationNotDateTimeMessage)
//                .withFieldName("dateOperation")
//                .critical();
    }
}
