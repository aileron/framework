/**
 * 
 */
package cc.aileron.generic;

/**
 * @author aileron
 * @param <Car>
 * @param <Cdr>
 * 
 */
public interface ConsCell<Car, Cdr>
{
    /**
     * @author aileron
     * @param <Car>
     * @param <Cdr>
     */
    class Value<Car, Cdr> implements ConsCell<Car, Cdr>
    {
        @Override
        public Car car()
        {
            return car;
        }

        @Override
        public Cdr cdr()
        {
            return cdr;
        }

        /**
         */
        public Value()
        {
        }

        /**
         * @param car
         * @param cdr
         */
        public Value(final Car car, final Cdr cdr)
        {
            this.car = car;
            this.cdr = cdr;
        }

        public Car car;
        public Cdr cdr;
    }

    /**
     * @return car
     */
    Car car();

    /**
     * @return cdr
     */
    Cdr cdr();
}
