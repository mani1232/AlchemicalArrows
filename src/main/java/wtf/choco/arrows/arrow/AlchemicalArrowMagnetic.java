package wtf.choco.arrows.arrow;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import wtf.choco.arrows.AlchemicalArrows;
import wtf.choco.arrows.api.AlchemicalArrowEntity;
import wtf.choco.arrows.api.property.ArrowProperty;
import wtf.choco.commons.util.MathUtil;

import java.util.Collection;
import java.util.Random;

public class AlchemicalArrowMagnetic extends ConfigurableAlchemicalArrow {

    public static final ArrowProperty PROPERTY_MAGNETISM_RADIUS = new ArrowProperty(AlchemicalArrows.key("magnetism_radius"), 5.0);

    private static final Random RANDOM = new Random();
    private static final BlockData IRON = Material.IRON_BLOCK.createBlockData(), GOLD = Material.GOLD_BLOCK.createBlockData();
    private static final int MAGNETISM_RADIUS_LIMIT = 10;

    public AlchemicalArrowMagnetic(AlchemicalArrows plugin) {
        super(plugin, "Magnetic", "&7Magnetic Arrow", 145);

        this.properties.setProperty(PROPERTY_MAGNETISM_RADIUS, () -> MathUtil.clamp(plugin.getConfig().getDouble("Arrow.Magnetic.Effect.MagnetismRadius", 5.0D), 0.0D, MAGNETISM_RADIUS_LIMIT));
    }

    @Override
    public void tick(AlchemicalArrowEntity arrow, Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.FALLING_DUST, location, 1, 0.1, 0.1, 0.1, IRON);
        if (RANDOM.nextInt(10) == 0) {
            world.spawnParticle(Particle.FALLING_DUST, location, 1, 0.1, 0.1, 0.1, GOLD);
        }

        // Validate in-tile arrow
        if (!arrow.getArrow().isInBlock()) {
            return;
        }

        double radius = properties.getProperty(PROPERTY_MAGNETISM_RADIUS).getAsDouble();
        if (radius <= 0.0) {
            return;
        }

        // Attract nearby entities
        Collection<Entity> nearbyEntities = world.getNearbyEntities(location, radius, radius, radius);
        if (nearbyEntities.isEmpty()) {
            return;
        }

        Vector arrowPosition = location.toVector();
        double radiusSquared = Math.pow(radius, 2);

        for (Entity entity : nearbyEntities) {
            if (entity.getType() != EntityType.DROPPED_ITEM) {
                continue;
            }

            Location entityLocation = entity.getLocation();
            Vector itemPosition = entityLocation.toVector();

            double relativeDistance = entityLocation.distanceSquared(location) / radiusSquared;
            if (relativeDistance <= 0.05) { // Cut off for the sake of performance. "Close enough" to arrow (0.2 blocks)
                continue;
            }

            Vector resultant = arrowPosition.subtract(itemPosition).normalize().multiply(relativeDistance / 10.0);
            entity.setVelocity(resultant);
        }
    }

    @Override
    public void onHitPlayer(AlchemicalArrowEntity arrow, Player player) {
        this.pullEntity(arrow.getArrow(), player);
    }

    @Override
    public void onHitEntity(AlchemicalArrowEntity arrow, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        this.pullEntity(arrow.getArrow(), livingEntity);
    }

    private void pullEntity(AbstractArrow source, LivingEntity entity) {
        Vector targetVelocity = source.getVelocity().multiply(-1);

        // No reason to exceed 4.0
        if (Math.abs(targetVelocity.getX()) > 4 || Math.abs(targetVelocity.getY()) > 4 || Math.abs(targetVelocity.getZ()) > 4) {
            targetVelocity.normalize().multiply(4);
        }

        entity.setVelocity(targetVelocity);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 2);
    }

}
