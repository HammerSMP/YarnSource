/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

import net.minecraft.util.ActionResult;

public class TypedActionResult<T> {
    private final ActionResult result;
    private final T value;

    public TypedActionResult(ActionResult arg, T object) {
        this.result = arg;
        this.value = object;
    }

    public ActionResult getResult() {
        return this.result;
    }

    public T getValue() {
        return this.value;
    }

    public static <T> TypedActionResult<T> success(T object) {
        return new TypedActionResult<T>(ActionResult.SUCCESS, object);
    }

    public static <T> TypedActionResult<T> consume(T object) {
        return new TypedActionResult<T>(ActionResult.CONSUME, object);
    }

    public static <T> TypedActionResult<T> pass(T object) {
        return new TypedActionResult<T>(ActionResult.PASS, object);
    }

    public static <T> TypedActionResult<T> fail(T object) {
        return new TypedActionResult<T>(ActionResult.FAIL, object);
    }

    public static <T> TypedActionResult<T> method_29237(T object, boolean bl) {
        return bl ? TypedActionResult.success(object) : TypedActionResult.consume(object);
    }
}

