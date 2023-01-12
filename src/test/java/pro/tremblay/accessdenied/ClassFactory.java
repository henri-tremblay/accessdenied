package pro.tremblay.accessdenied;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.ObjenesisHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ClassFactory {

    public static class MockMethodInterceptor {

        @SuppressWarnings("unused")
        @RuntimeType
        @BindingPriority(BindingPriority.DEFAULT * 2)
        public static Object interceptSuperCallable(
                @This Object obj,
                @Origin Method method,
                @AllArguments Object[] args,
                @SuperCall(serializableProxy = true) Callable<?> superCall) throws Throwable {
            return superCall.call();
        }

    }

    private static final AtomicInteger id = new AtomicInteger();

    private final Function<Class<?>, String> classNameProvider;
    private final Function<Class<?>, ClassLoader> classLoaderProvider;

    public static String samePackage(Class<?> clazz) {
        return clazz.getName();
    }

    public static String thisPackage(Class<?> clazz) {
        return "pro.tremblay.accessdenied.internal." + clazz.getSimpleName();
    }
    public static ClassLoader sameClassLoader(Class<?> clazz) {
        return clazz.getClassLoader();
    }

    public static ClassLoader thisClassLoader(Class<?> clazz) {
        return ClassFactory.class.getClassLoader();
    }

    public ClassFactory(Function<Class<?>, String> classNameProvider, Function<Class<?>, ClassLoader> classLoaderProvider) {
        this.classNameProvider = classNameProvider;
        this.classLoaderProvider = classLoaderProvider;
    }

    public <T> T wrap(Class<T> clazz) {
        Class<?> newClass = generateClass(clazz);
        return clazz.cast(ObjenesisHelper.newInstance(newClass));
    }

    private <T> Class<?> generateClass(Class<T> clazz) {
        ElementMatcher.Junction<MethodDescription> junction = ElementMatchers.any();

        try (DynamicType.Unloaded<T> unloaded = new ByteBuddy()
                .subclass(clazz)
                .name(classNameProvider.apply(clazz) + "$$$MyThing" + id.incrementAndGet())
                .method(junction)
                .intercept(MethodDelegation.to(MockMethodInterceptor.class))
                .make()) {
            return unloaded
                    .load(classLoaderProvider.apply(clazz), new ClassLoadingStrategy.ForUnsafeInjection())
                    .getLoaded();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
