
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package org.jsimpledb;

import com.google.common.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

import org.jsimpledb.annotation.JTransient;
import org.jsimpledb.util.AnnotationScanner;

/**
 * Support superclass for field annotation scanners.
 */
abstract class AbstractFieldScanner<T, A extends Annotation> extends AnnotationScanner<T, A> {

    protected final boolean autogenFields;
    protected final boolean autogenNonAbstract;

    AbstractFieldScanner(JClass<T> jclass, Class<A> annotationType, boolean autogenFields, boolean autogenNonAbstract) {
        super(jclass, annotationType);
        this.autogenFields = autogenFields;
        this.autogenNonAbstract = autogenNonAbstract;
    }

    protected abstract A getDefaultAnnotation();

    @Override
    protected A getAnnotation(Method method) {

        // Look for existing annotation
        final A annotation = super.getAnnotation(method);
        if (annotation != null)
            return annotation;

        // Check for auto-generated bean property getter method
        if (this.isAutoPropertyCandidate(method))
            return this.getDefaultAnnotation();

        // Skip this method
        return null;
    }

    protected boolean isAutoPropertyCandidate(Method method) {
        if (!this.autogenFields)
            return false;
        if ((method.getModifiers() & Modifier.STATIC) != 0)
            return false;
        if (this.hasJTransientAnnotation(method))
            return false;
        if (!this.autogenNonAbstract && this.isOverriddenByConcreteMethod(method))
            return false;
        if ((method.getModifiers() & (Modifier.PROTECTED | Modifier.PUBLIC)) == 0)
            return false;
        if ((method.getModifiers() & Modifier.PRIVATE) != 0)
            return false;
        if (!Pattern.compile("(is|get)(.+)").matcher(method.getName()).matches())
            return false;
        if (method.getParameterTypes().length != 0)
            return false;
        if (method.getReturnType() == Void.TYPE)
            return false;
        return true;
    }

    private boolean isOverriddenByConcreteMethod(Method method) {
        if ((method.getModifiers() & Modifier.ABSTRACT) == 0)                   // quick check
            return true;
        final Class<?> methodType = method.getDeclaringClass();
        for (TypeToken<?> typeToken : TypeToken.of(this.jclass.type).getTypes()) {
            final Class<?> type = typeToken.getRawType();
            if (!methodType.isAssignableFrom(type) || type.equals(methodType))  // i.e., type > methodType
                continue;
            final Method otherMethod;
            try {
                otherMethod = type.getDeclaredMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                continue;
            }
            if ((otherMethod.getModifiers() & Modifier.ABSTRACT) == 0)
                return true;
        }
        return false;
    }

    private boolean hasJTransientAnnotation(Method method) {
        final String name = method.getName();
        final Class<?>[] ptypes = method.getParameterTypes();
        for (TypeToken<?> typeToken : TypeToken.of(method.getDeclaringClass()).getTypes()) {
            final Method override;
            try {
                override = typeToken.getRawType().getDeclaredMethod(name, ptypes);
            } catch (NoSuchMethodException e) {
                continue;
            }
            if (override.getAnnotation(JTransient.class) != null)
                return true;
        }
        return false;
    }
}
