package com.edu.moneywayapi.webApi.validator;

import br.com.fluentvalidator.AbstractValidator;
import br.com.fluentvalidator.context.Error;
import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static br.com.fluentvalidator.predicate.LogicalPredicate.not;
import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;
import static br.com.fluentvalidator.predicate.StringPredicate.stringMatches;

@Component
public class UserValidator extends AbstractValidator<UserDTO> {

    @Value("${user.email.empty}")
    private String isEmailEmptyMessage;

    @Value("${user.email.not-match}")
    private String isEmailNotMatchFormatMessage;

    @Value("${format.email}")
    private String formatEmail;

    @Value("${user.login.empty}")
    private String isLoginEmptyMessage;

    @Value("${user.login.size.min}")
    private Integer minSizeLogin;

    @Value("${user.login.size.max}")
    private Integer maxSizeLogin;

    @Value("${user.login.size.message}")
    private String isIncorrectSizeLoginMessage;

    @Value("${user.password.empty}")
    private String isPasswordEmptyMessage;

    @Value("${user.password.size.min}")
    private Integer minSizePassword;

    @Value("${user.password.size.max}")
    private Integer maxSizePassword;

    @Value("${user.password.size.message}")
    private String isIncorrectSizePasswordMessage;

    @Override
    public ValidationResult validate(UserDTO instance) {
        ValidationResult validationResult = super.validate(instance);
        if (!validationResult.isValid())
            return validationResult;

        Collection<Error> errors = new ArrayList<>();

        String username = instance.getLogin();
        if (minSizeLogin > username.length() || username.length() > maxSizeLogin)
            errors.add(Error.create("login", isIncorrectSizeLoginMessage, null, null));

        String password = instance.getPassword();
        if (minSizePassword > password.length() || password.length() > maxSizePassword)
            errors.add(Error.create("password", isIncorrectSizePasswordMessage, null, null));

        if (errors.isEmpty())
            return ValidationResult.ok();

        return ValidationResult.fail(errors);
    }

    @Override
    public void rules() {
        ruleFor(UserDTO::getEmail)
                .must(not(stringEmptyOrNull()))
                .withMessage(isEmailEmptyMessage)
                .withFieldName("email")
                .critical();

        ruleFor(UserDTO::getEmail)
                .must(stringMatches(formatEmail))
                .withMessage(isEmailNotMatchFormatMessage)
                .withFieldName("email")
                .critical();

        ruleFor(UserDTO::getLogin)
                .must(not(stringEmptyOrNull()))
                .withMessage(isLoginEmptyMessage)
                .withFieldName("login")
                .critical();

        ruleFor(UserDTO::getPassword)
                .must(not(stringEmptyOrNull()))
                .withMessage(isPasswordEmptyMessage)
                .withFieldName("password")
                .critical();
    }
}
