
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package org.jsimpledb;

import com.google.common.base.Preconditions;

import java.util.Arrays;

import javax.validation.ValidatorFactory;

import org.jsimpledb.core.Database;
import org.jsimpledb.kv.simple.SimpleKVDatabase;

/**
 * Factory for {@link JSimpleDB} instances.
 *
 * <p>
 * If no {@link Database} is configured, newly created {@link JSimpleDB} instances will use an initially empty,
 * in-memory {@link SimpleKVDatabase}.
 *
 * @see JSimpleDB
 */
public class JSimpleDBFactory {

    private Database database;
    private int schemaVersion;
    private StorageIdGenerator storageIdGenerator = new DefaultStorageIdGenerator();
    private Iterable<? extends Class<?>> modelClasses;
    private ValidatorFactory validatorFactory;

    /**
     * Configure the Java model classes.
     *
     * <p>
     * Note: {@link org.jsimpledb.annotation.JSimpleClass &#64;JSimpleClass}-annotated super-types of any
     * class in {@code modelClasses} will be included, even if the super-type is not explicitly specified in {@code modelClasses}.
     *
     * @param modelClasses classes annotated with {@link org.jsimpledb.annotation.JSimpleClass &#64;JSimpleClass} annotations
     * @return this instance
     */
    public JSimpleDBFactory setModelClasses(Iterable<? extends Class<?>> modelClasses) {
        this.modelClasses = modelClasses;
        return this;
    }

    /**
     * Configure the Java model classes.
     *
     * <p>
     * Equivalent to {@link #setModelClasses(Iterable) setModelClasses}{@code (Arrays.asList(modelClasses))}.
     *
     * @param modelClasses classes annotated with {@link org.jsimpledb.annotation.JSimpleClass &#64;JSimpleClass} annotations
     * @return this instance
     * @see #setModelClasses(Iterable)
     */
    public JSimpleDBFactory setModelClasses(Class<?>... modelClasses) {
        return this.setModelClasses(Arrays.asList(modelClasses));
    }

    /**
     * Configure the underlying {@link Database} for this instance.
     *
     * <p>
     * By default this instance will use an initially empty, in-memory {@link SimpleKVDatabase}.
     *
     * @param database core API database to use
     * @return this instance
     */
    public JSimpleDBFactory setDatabase(Database database) {
        this.database = database;
        return this;
    }

    /**
     * Configure the schema version number associated with the configured Java model classes.
     *
     * <p>
     * A value of zero means to use whatever is the highest version already recorded in the database.
     * However, if this instance has no {@link Database} configured, then an empty
     * {@link SimpleKVDatabase} is used and therefore a schema version of {@code 1} is assumed.
     *
     * @param schemaVersion the schema version number of the schema derived from the configured Java model classes,
     *  or zero to default to the highest version already recorded in the database
     * @return this instance
     */
    public JSimpleDBFactory setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }

    /**
     * Configure the {@link StorageIdGenerator} for auto-generating storage ID's when not explicitly
     * specified in {@link org.jsimpledb.annotation.JSimpleClass &#64;JSimpleClass},
     * {@link org.jsimpledb.annotation.JField &#64;JField}, etc., annotations.
     *
     * <p>
     * This instance will initially be configured with a {@link DefaultStorageIdGenerator}.
     * To disable auto-generation of storage ID's altogether, configure a null value here.
     *
     * @param storageIdGenerator storage ID generator, or null to disable auto-generation of storage ID's
     * @return this instance
     */
    public JSimpleDBFactory setStorageIdGenerator(StorageIdGenerator storageIdGenerator) {
        this.storageIdGenerator = storageIdGenerator;
        return this;
    }

    /**
     * Configure a custom {@link ValidatorFactory} used to create {@link javax.validation.Validator}s
     * for validation within transactions.
     *
     * @param validatorFactory factory for validators
     * @return this instance
     * @throws IllegalArgumentException if {@code validatorFactory} is null
     */
    public JSimpleDBFactory setValidatorFactory(ValidatorFactory validatorFactory) {
        Preconditions.checkArgument(validatorFactory != null, "null validatorFactory");
        this.validatorFactory = validatorFactory;
        return this;
    }

    /**
     * Construct a {@link JSimpleDB} instance using this instance's configuration.
     *
     * @return newly created {@link JSimpleDB} database
     * @throws IllegalArgumentException if this instance has an incomplete or invalid configuration
     * @throws IllegalArgumentException if any Java model class has an invalid annotation
     */
    public JSimpleDB newJSimpleDB() {
        Database database1 = this.database;
        int schemaVersion1 = this.schemaVersion;
        if (database1 == null) {
            database1 = new Database(new SimpleKVDatabase());
            if (schemaVersion1 == 0)
                schemaVersion1 = 1;
        }
        final JSimpleDB jdb = new JSimpleDB(database1, schemaVersion1, this.storageIdGenerator, this.modelClasses);
        if (this.validatorFactory != null)
            jdb.setValidatorFactory(this.validatorFactory);
        return jdb;
    }
}

