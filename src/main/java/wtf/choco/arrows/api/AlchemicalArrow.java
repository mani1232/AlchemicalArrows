package wtf.choco.arrows.api;

import java.util.Objects;

import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;

import wtf.choco.arrows.api.property.ArrowProperty;
import wtf.choco.arrows.api.property.PropertyMap;

/**
 * Represents the base of an alchemical arrow with special effects upon hitting a
 * block, entity or player
 * 
 * @author Parker Hawke - 2008Choco
 */
public abstract class AlchemicalArrow implements Keyed {
	
	protected final PropertyMap properties = new PropertyMap();
	
	/**
	 * Get the display name of this alchemical arrow. This includes colour codes and formatting.
	 * The returned String should be expected in messages sent to players
	 * 
	 * @return the arrow's display name
	 */
	public abstract String getDisplayName();
	
	/**
	 * Get the item representation of this alchemical arrow. The type must be
	 * of {@link Material#ARROW} and unique to this type, otherwise an exception
	 * will be thrown. If this item is present in the player's inventory whilst
	 * attempting to shoot a bot, this arrow type will be used instead of a
	 * regular arrow.
	 * 
	 * @return the arrow item
	 */
	public abstract ItemStack getItem();
	
	/**
	 * Get a map containing all properties for this arrow
	 * 
	 * @return the arrow properties
	 */
	public final PropertyMap getProperties() {
		return properties;
	}
	
	/** 
	 * Called 20 times every second. This method is intended for displaying particles around
	 * the arrow, performing tasks whilst the arrow is still in the world, etc.
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param location the arrow's current location at this tick
	 */
	public void tick(AlchemicalArrowEntity arrow, Location location) { }
	
	/** 
	 * Called when the arrow hits a solid block
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param block the block on which the arrow landed
	 */
	public void onHitBlock(AlchemicalArrowEntity arrow, Block block) { }
	
	/** 
	 * Called when the arrow hits a player
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param player the player damaged by the arrow
	 */
	public void onHitPlayer(AlchemicalArrowEntity arrow, Player player) { }
	
	/** 
	 * Called when the arrow hits an entity (this excludes Players. For Players, see
	 * {@link #onHitPlayer(AlchemicalArrowEntity, Player)})
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param entity the entity damaged by the arrow
	 */
	public void onHitEntity(AlchemicalArrowEntity arrow, Entity entity) { }
	
	/** 
	 * Called at low priority when a player has successfully shot this alchemical arrow,
	 * but it has yet to be registered. Such that this method returns true, the alchemical
	 * arrow will be launched
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param player the player that shot the arrow
	 * 
	 * @return whether the shot should be permitted or not
	 */
	public boolean onShootFromPlayer(AlchemicalArrowEntity arrow, Player player) {
		return true;
	}
	
	/** 
	 * Called at a low priority when a {@link Skeleton} successfully shoots an arrow,
	 * but it has yet to be registered. Such that this method returns true, the alchemical
	 * arrow will be launched. The {@link ArrowProperty#SKELETONS_CAN_SHOOT} property must
	 * return true in order for this method to be invoked
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param skeleton the skeleton that shot the arrow
	 * 
	 * @return whether the shot should be permitted or not
	 */
	public boolean onShootFromSkeleton(AlchemicalArrowEntity arrow, Skeleton skeleton) {
		return true;
	}
	
	/** 
	 * Called at a low priority when a {@link BlockProjectileSource} (i.e. Dispenser) 
	 * shoots an arrow, but it has yet to be registered. Such that this method returns
	 * true, the alchemical arrow will be launched
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param source the block source that shot the arrow
	 * 
	 * @return whether the shot should be permitted or not
	 */
	public boolean onShootFromBlockSource(AlchemicalArrowEntity arrow, BlockProjectileSource source) {
		return true;
	}
	
	/** 
	 * Called the instant before {@link #onHitPlayer(AlchemicalArrowEntity, Player)} or
	 * {@link #onHitEntity(AlchemicalArrowEntity, Entity)} is called. Used to cancel events
	 * if necessary
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param event the EntityDamageByEntityEvent source
	 */
	public void hitEntityEventHandler(AlchemicalArrowEntity arrow, EntityDamageByEntityEvent event) { }
	
	/** 
	 * Called the instant before {@link #onHitBlock(AlchemicalArrowEntity, Block)} is called.
	 * Used to cancel events if necessary
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param event the ProjectileHitEvent source
	 */
	public void hitGroundEventHandler(AlchemicalArrowEntity arrow, ProjectileHitEvent event) { }
	
	/** 
	 * Called the instant before {@link #onShootFromPlayer(AlchemicalArrowEntity, Player)},
	 * {@link #onShootFromSkeleton(AlchemicalArrowEntity, Skeleton)} or
	 * {@link #onShootFromBlockSource(AlchemicalArrowEntity, BlockProjectileSource)} is called.
	 * Used to cancel events if necessary
	 * 
	 * @param arrow the alchemical arrow entity instance
	 * @param event the ProjectileLaunchEvent source
	 */
	public void shootEventHandler(AlchemicalArrowEntity arrow, ProjectileLaunchEvent event) { }
	
	/** 
	 * Create a new instance of an {@link AlchemicalArrowEntity}. If a custom AlchemicalArrowEntity
	 * implementation is used, this method must be overridden to return a custom instance of it.
	 * Under no circumstance should additional, non-arrow entity-related code be executed in an
	 * overridden implementation of this method. For additional logic, see AlchemicalArrow's various
	 * methods and override them where required
	 * 
	 * @param arrow the Bukkit {@link Arrow} instance from which to create an AlchemicalArrowEntity
	 * @return the new AlchemicalArrowEntity instance of this implementation
	 */
	public AlchemicalArrowEntity createNewArrow(Arrow arrow) {
		return new AlchemicalArrowEntity(this, arrow);
	}
	
	@Override
	public int hashCode() {
		return 31 * (getKey() == null ? 0 : getKey().hashCode());
	}
	
	@Override
	public boolean equals(Object object) {
		return object == this || (object instanceof AlchemicalArrow && Objects.equals(getKey(), ((AlchemicalArrow) object).getKey()));
	}
	
}