package wtf.choco.arrows.arrow;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import wtf.choco.arrows.AlchemicalArrows;
import wtf.choco.arrows.api.AlchemicalArrowEntity;
import wtf.choco.arrows.api.property.ArrowProperty;

public class AlchemicalArrowEnder extends ConfigurableAlchemicalArrow {

    public static final ArrowProperty PROPERTY_TELEPORT_ON_HIT_BLOCK = new ArrowProperty(AlchemicalArrows.key("teleport_on_hit_block"), true);

    public AlchemicalArrowEnder(AlchemicalArrows plugin) {
        super(plugin, "Ender", "&5Ender Arrow", 137);

        this.properties.setProperty(PROPERTY_TELEPORT_ON_HIT_BLOCK, () -> plugin.getConfig().getBoolean("Arrow.Ender.Effect.TeleportOnHitBlock", true));
    }

    @Override
    public void tick(AlchemicalArrowEntity arrow, Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.PORTAL, location, 3, 0.1, 0.1, 0.1);
    }

    @Override
    public void onHitPlayer(AlchemicalArrowEntity arrow, Player player) {
        AbstractArrow bukkitArrow = arrow.getArrow();
        if (!(bukkitArrow.getShooter() instanceof LivingEntity livingEntity)) {
            return;
        }

        this.swapLocations(bukkitArrow, livingEntity, player);
    }

    @Override
    public void onHitEntity(AlchemicalArrowEntity arrow, Entity entity) {
        AbstractArrow bukkitArrow = arrow.getArrow();
        if (!(bukkitArrow.getShooter() instanceof LivingEntity livingShooter) || !(entity instanceof LivingEntity livingEntity) || entity.getType() == EntityType.ARMOR_STAND) {
            return;
        }

        this.swapLocations(bukkitArrow, livingShooter, livingEntity);
    }

    @Override
    public void onHitBlock(AlchemicalArrowEntity arrow, Block block) {
        if (!properties.getProperty(PROPERTY_TELEPORT_ON_HIT_BLOCK).getAsBoolean()) {
            return;
        }

        ProjectileSource shooter = arrow.getArrow().getShooter();
        if (!(shooter instanceof LivingEntity shooterEntity)) {
            return;
        }

        Location shooterLocation = shooterEntity.getLocation();

        arrow.getArrow().remove(); // Remove the arrow before we teleport the player

        Location teleportLocation = block.getLocation().add(0.5, 1, 0.5);
        teleportLocation.setPitch(shooterLocation.getPitch());
        teleportLocation.setYaw(shooterLocation.getYaw());
        shooterEntity.teleport(teleportLocation);
    }

    private void swapLocations(AbstractArrow source, LivingEntity shooter, LivingEntity target) {
        source.setKnockbackStrength(0);

        Location targetLocation = target.getLocation();
        Vector targetVelocity = target.getVelocity();

        // Swap player locations
        target.teleportAsync(shooter.getLocation());
        target.setVelocity(shooter.getVelocity());
        shooter.teleportAsync(targetLocation);
        shooter.setVelocity(targetVelocity);

        // Play sounds and display particles
        World world = source.getWorld();
        world.playSound(source.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 3);
        world.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 3);
        world.spawnParticle(Particle.PORTAL, source.getLocation(), 50, 1, 1, 1);
        world.spawnParticle(Particle.PORTAL, target.getLocation(), 5, 1, 1, 1);
    }

}
