package dagger;

import play.api.inject.ApplicationLifecycle;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.data.validation.DefaultConstraintValidatorFactory;
import play.data.validation.ValidatorProvider;
import play.i18n.MessagesApi;
import play.inject.DelegateApplicationLifecycle;
import play.inject.Injector;

import javax.inject.Singleton;
import javax.validation.ConstraintValidatorFactory;

@Module
public class JavaFormsModule {

    @Singleton
    @Provides
    public FormFactory providesFormFactory(MessagesApi messagesApi, Formatters formatters, ValidatorProvider validator) {
        return new FormFactory(messagesApi, formatters, validator.get());
    }

    @Singleton
    @Provides
    public Formatters providesFormatters(MessagesApi messagesApi) {
        return new Formatters(messagesApi);
    }

    @Singleton
    @Provides
    public ValidatorProvider providesValidator(ConstraintValidatorFactory factory, ApplicationLifecycle lifecycle) {
        return new ValidatorProvider(factory, new DelegateApplicationLifecycle(lifecycle));
    }

    @Singleton
    @Provides
    public ConstraintValidatorFactory constraintValidatorFactory(Injector injector) {
        return new DefaultConstraintValidatorFactory(injector);
    }
}
