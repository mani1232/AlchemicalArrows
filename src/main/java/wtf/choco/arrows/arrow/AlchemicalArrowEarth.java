package wtf.choco.arrows.arrow;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import wtf.choco.arrows.AlchemicalArrows;
import wtf.choco.arrows.api.AlchemicalArrowEntity;

public class AlchemicalArrowEarth extends ConfigurableAlchemicalArrow {

    private static final PotionEffect SLOWNESS_EFFECT = new PotionEffect(PotionEffectType.SLOW, 100, 1);
    private static final BlockData DIRT = Material.DIRT.createBlockData();

    public AlchemicalArrowEarth(AlchemicalArrows plugin) {
        super(plugin, "Earth", "&7Earth Arrow", 136);
    }

    @Override
    public void tick(AlchemicalArrowEntity arrow, Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.BLOCK_DUST, location, 1, 0.1, 0.1, 0.1, 0.1, DIRT);
    }

    @Override
    public void onHitPlayer(AlchemicalArrowEntity arrow, Player player) {
        this.buryEntity(player);
    }

    @Override
    public void onHitEntity(AlchemicalArrowEntity arrow, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        this.buryEntity(livingEntity);
    }

    private void buryEntity(LivingEntity entity) {
        Location location = entity.getLocation();
        while (location.getBlockY() >= 1 && !location.getBlock().getType().isSolid()) {
            location.subtract(0, 1, 0);
        }

        // Don't drop them down if there's no block to catch them
        if (location.getBlock().getRelative(BlockFace.DOWN).isEmpty()) {
            return;
        }

        // Round to block coordinate and add 0.5 (centre coordinates)
        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
        entity.teleport(location);

        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 0.75F);
        entity.addPotionEffect(SLOWNESS_EFFECT);
    }

}
