
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package org.jsimpledb.parse.expr;

import com.google.common.base.Preconditions;

import java.lang.reflect.Field;

import org.jsimpledb.parse.ParseSession;

/**
 * {@link Value} that reflects a Java field in some class or object.
 */
abstract class AbstractFieldValue extends AbstractValue implements LValue {

    protected final Field field;

    protected AbstractFieldValue(Field field) {
        Preconditions.checkArgument(field != null, "null field");
        this.field = field;
    }

    @Override
    public Class<?> getType(ParseSession session) {
        return this.field.getType();
    }
}

