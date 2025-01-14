package wtf.choco.arrows.api;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an in-world {@link AlchemicalArrow} implementation. This instance wraps a
 * Bukkit {@link Arrow} and may be extended to hold arrow-implementation-specific data.
 * If this class is extended, the {@link AlchemicalArrow#createNewArrow(Arrow)} method
 * in the AlchemcialArrow implementation must be overwritten to return an instance of
 * the custom implementation in order to be recognised by the AlchemicalArrows plugin
 *
 * @author Parker Hawke - Choco
 */
public class AlchemicalArrowEntity {

    protected final AlchemicalArrow implementation;
    protected final Arrow arrow;

    /**
     * Construct a new instance of AlchemicalArrowEntity given a type and arrow
     *
     * @param implementation the alchemical arrow implementation to create
     * @param arrow          the Bukkit arrow to wrap
     */
    public AlchemicalArrowEntity(@NotNull AlchemicalArrow implementation, @NotNull Arrow arrow) {
        Preconditions.checkNotNull(implementation, "Cannot create an arrow of type null");
        Preconditions.checkNotNull(arrow, "Cannot wrap a null org.bukkit.entity.Arrow");

        this.implementation = implementation;
        this.arrow = arrow;
    }

    /**
     * Get this AlchemicalArrowEntity's implementation (i.e. the underlying {@link AlchemicalArrow})
     *
     * @return the alchemical arrow implementation
     */
    @NotNull
    public final AlchemicalArrow getImplementation() {
        return implementation;
    }

    /**
     * Get this AlchemicalArrowEntity's wrapped {@link AbstractArrow} instance, its underlying instance
     *
     * @return the wrapped arrow
     */
    @NotNull
    public final AbstractArrow getArrow() {
        return arrow;
    }

    /**
     * Get the location of this arrow. This is equivalent to invoking
     * {@code getArrow().getLocation()}
     *
     * @return the arrow's location
     */
    @NotNull
    public final Location getLocation() {
        return arrow.getLocation();
    }

    /**
     * Get the world in which this arrow resides. This is equivalent to invoking
     * {@code getArrow().getWorld()}
     *
     * @return the arrow's world
     */
    @NotNull
    public final World getWorld() {
        return arrow.getWorld();
    }

    @Override
    public int hashCode() {
        int prime = 31;

        int result = prime + (implementation != null ? implementation.hashCode() : 0);
        result = prime * result + (arrow != null ? arrow.hashCode() : 0);

        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof AlchemicalArrowEntity other)) {
            return false;
        }

        return Objects.equals(implementation, other.implementation) && Objects.equals(arrow, other.arrow);
    }

}
