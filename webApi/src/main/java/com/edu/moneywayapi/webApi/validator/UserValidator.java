package com.edu.moneywayapi.webApi.validator;

import br.com.fluentvalidator.AbstractValidator;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${user.password.empty}")
    private String isPasswordEmptyMessage;

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
