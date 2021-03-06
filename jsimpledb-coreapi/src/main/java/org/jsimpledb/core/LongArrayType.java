
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package org.jsimpledb.core;

import com.google.common.primitives.Longs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * {@code long[]} primitive array type. Does not support null arrays.
 */
class LongArrayType extends IntegralArrayType<long[], Long> {

    private static final long serialVersionUID = 7577070533837522681L;

    LongArrayType() {
       super(FieldTypeRegistry.LONG, long[].class);
    }

    @Override
    protected int getArrayLength(long[] array) {
        return array.length;
    }

    @Override
    protected Long getArrayElement(long[] array, int index) {
        return array[index];
    }

    @Override
    protected long[] createArray(List<Long> elements) {
        return Longs.toArray(elements);
    }

    @Override
    protected void encode(long[] array, DataOutputStream output) throws IOException {
        for (int i = 0; i < array.length; i++)
            output.writeLong(array[i]);
    }

    @Override
    protected long[] decode(DataInputStream input, int numBytes) throws IOException {
        final long[] array = this.checkDecodeLength(numBytes);
        for (int i = 0; i < array.length; i++)
            array[i] = input.readLong();
        return array;
    }
}

