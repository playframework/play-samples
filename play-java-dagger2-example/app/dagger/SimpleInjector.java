package dagger;

import play.api.inject.BindingKey;
import play.inject.Injector;
import scala.reflect.ClassTag;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A simple injector with additional classes...
 */
public class SimpleInjector implements Injector {
    private final Injector injector;
    private final Map<Class, Supplier<Object>> mappings;

    public SimpleInjector(Injector injector, Map<Class, Supplier<Object>> mappings) {
        this.injector = injector;
        this.mappings = mappings;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T instanceOf(Class<T> clazz) {
        try {
            return injector.instanceOf(clazz);
        } catch (Throwable e) {
            Supplier<Object> objectSupplier = mappings.get(clazz);
            if (objectSupplier != null) {
                return (T) objectSupplier.get();
            } else {
                return null;
            }
        }
    }

    @Override
    public <T> T instanceOf(BindingKey<T> key) {
        return instanceOf(key.clazz());
    }

    @Override
    public play.api.inject.Injector asScala() {
        Injector thisInjector = this;
        return new play.api.inject.Injector() {
            @Override
            public Injector asJava() {
                return thisInjector;
            }

            @Override
            public <T> T instanceOf(BindingKey<T> key) {
                return thisInjector.instanceOf(key);
            }

            @Override
            public <T> T instanceOf(Class<T> clazz) {
                return thisInjector.instanceOf(clazz);
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T instanceOf(ClassTag<T> evidence) {
                return thisInjector.instanceOf((Class<T>) evidence.runtimeClass());
            }
        };
    }
}
