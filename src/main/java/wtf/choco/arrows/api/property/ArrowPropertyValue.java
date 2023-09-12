package wtf.choco.arrows.api.property;

/**
 * Represents a transformable value of an {@link ArrowProperty}.
 *
 * @author Parker Hawke - Choco
 */
public interface ArrowPropertyValue {

    /**
     * Get this value as a primitive int.
     *
     * @return an int
     */
    int getAsInt();

    /**
     * Get this value as a primitive float.
     *
     * @return a float
     */
    float getAsFloat();

    /**
     * Get this value as a primitive double.
     *
     * @return a double
     */
    double getAsDouble();

    /**
     * Get this value as a primitive long.
     *
     * @return a long
     */
    long getAsLong();

    /**
     * Get this value as a primitive short.
     *
     * @return a short
     */
    short getAsShort();

    /**
     * Get this value as a primitive byte.
     *
     * @return a byte
     */
    byte getAsByte();

    /**
     * Get this value as a primitive boolean.
     *
     * @return a boolean
     */
    boolean getAsBoolean();

    /**
     * Get this value as a String.
     *
     * @return a string
     */
    String getAsString();

    /**
     * Get this value.
     *
     * @return the value
     */
    Object getValue();

}
