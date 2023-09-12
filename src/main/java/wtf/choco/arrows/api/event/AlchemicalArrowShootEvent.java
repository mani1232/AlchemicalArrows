package wtf.choco.arrows.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import wtf.choco.arrows.api.AlchemicalArrowEntity;

/**
 * Called when an alchemical arrow has been shot.
 *
 * @author Parker Hawke - Choco
 */
public class AlchemicalArrowShootEvent extends AlchemicalArrowEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ProjectileSource shooter;
    private boolean cancelled = false;

    /**
     * Construct a new AlchemicalArrowShootEvent
     *
     * @param arrow   the arrow that was shot
     * @param shooter the source of the arrow
     */
    public AlchemicalArrowShootEvent(@NotNull AlchemicalArrowEntity arrow, @NotNull ProjectileSource shooter) {
        super(arrow);
        this.shooter = shooter;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the projectile source that launched this arrow
     *
     * @return the shooter
     */
    @NotNull
    public ProjectileSource getShooter() {
        return shooter;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
