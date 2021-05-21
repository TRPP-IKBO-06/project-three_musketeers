package main.stager.utils;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Utilits {

    public static boolean isNullOrEmpty(Collection<?> value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isBlank(@NotNull String value) {
        return value.trim().isEmpty();
    }

    public static boolean isNullOrBlank(String value) {
        return value == null || isBlank(value);
    }

    public static <T> T getDefaultOnNull(T original, T defaultValue) {
        return original == null ? defaultValue : original;
    }

    public static String getDefaultOnNullOrEmpty(String original, String defaultValue) {
        return isNullOrEmpty(original) ? defaultValue : original;
    }

    public static String getDefaultOnNullOrBlank(String original, String defaultValue) {
        return isNullOrBlank(original) ? defaultValue : original;
    }

    @FunctionalInterface
    public interface IModifier<T> { T apply(T value); }

    @FunctionalInterface
    public interface IPredicate<T> { boolean apply(T value); }

    public static <T> List<T> filter(List<T> target, IPredicate<T> predicate) {
        if (isNullOrEmpty(target))
            return target;
        List<T> result = new ArrayList<>();
        for (T element: target)
            if (predicate.apply(element))
                result.add(element);
        return result;
    }

    public static <T> List<T> map(List<T> target, IModifier<T> modifier) {
        if (isNullOrEmpty(target))
            return target;
        List<T> result = new ArrayList<>();
        for (T element: target)
            result.add(modifier.apply(element));
        return result;
    }
}
