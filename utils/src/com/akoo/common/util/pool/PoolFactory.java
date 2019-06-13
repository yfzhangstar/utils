package com.akoo.common.util.pool;


/**
 * Factory and utility methods for
 * <p>
 * {@link Pool} and {@link BlockingPool} classes
 * <p>
 * defined in this package.
 * This class supports the following kinds of methods:
 *
 *
 * <ul>
 *
 * <li> Method that creates and returns a default non-blocking
 * implementation of the {@link Pool} interface.
 * </li>
 *
 *
 * <li> Method that creates and returns a
 * default implementation of
 * the {@link BlockingPool} interface.
 * </li>
 *
 * </ul>
 *
 */
public final class PoolFactory<T> {
    private PoolFactory() {
    }

    /**
     * Creates a and returns a new object pool,
     * that is an implementation of the {@link BlockingPool},
     * whose size is limited by
     * the <tt> size </tt> parameter.
     *
     * @param size      the number of objects in the pool.
     * @param factory   the factory to create new objects.
     * @param validator the validator to
     *                  validate the re-usability of returned objects.
     * @return a blocking object pool
     * bounded by <tt> size </tt>
     */
    public static <T> Pool<T> newBoundedBlockingPool(
            int size,
            ObjectFactory<T> factory,
            Pool.Validator<T> validator) {
        return new BoundedBlockingPool(size, validator, factory);
    }

    /**
     * Creates a and returns a new object pool,
     * that is an implementation of the {@link Pool}
     * whose size is limited
     * by the <tt> size </tt> parameter.
     *
     * @param size the number of objects in the pool.
     * @param factory the factory to create new objects.
     * @param validator the validator to validate
     * the re-usability of returned objects.
     *
     * @return an object pool bounded by <tt> size </tt>
     */
    public static <T> Pool<T> newBoundedNonBlockingPool(
            int size,
            ObjectFactory<T> factory,
            Pool.Validator<T> validator) {
        return new BoundedPool(size, validator, factory);
    }
}
