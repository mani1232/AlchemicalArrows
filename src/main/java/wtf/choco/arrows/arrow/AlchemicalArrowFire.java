package wtf.choco.arrows.arrow;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import wtf.choco.arrows.AlchemicalArrows;
import wtf.choco.arrows.api.AlchemicalArrowEntity;
import wtf.choco.arrows.api.property.ArrowProperty;
import wtf.choco.arrows.arrow.entity.ArrowEntityFire;

public class AlchemicalArrowFire extends ConfigurableAlchemicalArrow {

    public static final ArrowProperty PROPERTY_EXTINGUISHABLE = new ArrowProperty(AlchemicalArrows.key("extinguishable"), true);
    public static final ArrowProperty PROPERTY_TICKS_TO_MELT = new ArrowProperty(AlchemicalArrows.key("ticks_to_melt"), 60);

    private static final Random RANDOM = new Random();

    private final AlchemicalArrows plugin;

    public AlchemicalArrowFire(AlchemicalArrows plugin) {
        super(plugin, "Fire", "&cFire Arrow", 139);

        this.plugin = plugin;

        this.properties.setProperty(ArrowProperty.SKELETONS_CAN_SHOOT, () -> plugin.getConfig().getBoolean("Arrow.Fire.Skeleton.CanShoot", true));
        this.properties.setProperty(ArrowProperty.ALLOW_INFINITY, () -> plugin.getConfig().getBoolean("Arrow.Fire.AllowInfinity", false));
        this.properties.setProperty(ArrowProperty.SKELETON_LOOT_WEIGHT, () -> plugin.getConfig().getDouble("Arrow.Fire.Skeleton.LootDropWeight", 10.0));

        this.properties.setProperty(PROPERTY_EXTINGUISHABLE, () -> plugin.getConfig().getBoolean("Arrow.Fire.Effect.Extinguishable", true));
        this.properties.setProperty(PROPERTY_TICKS_TO_MELT, () -> plugin.getConfig().getInt("Arrow.Fire.Effect.TicksToMelt", 60));
    }

    @Override
    public void tick(AlchemicalArrowEntity arrow, Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        ArrowEntityFire fireArrow = (ArrowEntityFire) arrow;
        if (!fireArrow.isExtinguished()) {
            world.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0.1, 0.1, 0.1, 0.001);
            world.spawnParticle(Particle.FLAME, location, 1, 0.1, 0.1, 0.1, 0.001);

            // Extinguish fire arrows when in water
            if (properties.getProperty(PROPERTY_EXTINGUISHABLE).getAsBoolean()) {
                BlockData currentBlockData;
                Block currentBlock = location.getBlock();

                if (currentBlock.getType() == Material.WATER || ((currentBlockData = currentBlock.getBlockData()) instanceof Waterlogged && ((Waterlogged) currentBlockData).isWaterlogged())) {
                    fireArrow.setExtinguished(true);
                    world.playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0F, 1.2F);
                }
            }
        }

        fireArrow.tick();
    }

    @Override
    public void onHitPlayer(AlchemicalArrowEntity arrow, Player player) {
        if (((ArrowEntityFire) arrow).isExtinguished()) {
            return;
        }

        player.setFireTicks(40 + RANDOM.nextInt(61));
    }

    @Override
    public void onHitEntity(AlchemicalArrowEntity arrow, Entity entity) {
        if (((ArrowEntityFire) arrow).isExtinguished()) {
            return;
        }

        entity.setFireTicks(40 + RANDOM.nextInt(61));
    }

    @Override
    @NotNull
    public AlchemicalArrowEntity createNewArrow(@NotNull Arrow arrow) {
        return new ArrowEntityFire(this, arrow, plugin, properties.getProperty(PROPERTY_TICKS_TO_MELT).getAsInt());
    }

}
