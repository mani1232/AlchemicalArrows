package wtf.choco.arrows.arrow;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import wtf.choco.arrows.AlchemicalArrows;
import wtf.choco.arrows.api.AlchemicalArrowEntity;
import wtf.choco.arrows.api.property.ArrowProperty;

public class AlchemicalArrowDeath extends AlchemicalArrowAbstract {

	private static final PotionEffect WITHER_EFFECT = new PotionEffect(PotionEffectType.WITHER, 100, 2);
	private static final Random RANDOM = new Random();

	private final FileConfiguration config;

	public AlchemicalArrowDeath(AlchemicalArrows plugin) {
		super(plugin, "death", c -> c.getString("Arrow.Death.Item.DisplayName", "&0Death Arrow"), c -> c.getStringList("Arrow.Death.Item.Lore"));

		this.config = plugin.getConfig();
		this.properties.setProperty(ArrowProperty.SKELETONS_CAN_SHOOT, config.getBoolean("Arrow.Death.Skeleton.CanShoot", true));
		this.properties.setProperty(ArrowProperty.ALLOW_INFINITY, config.getBoolean("Arrow.Death.AllowInfinity", false));
		this.properties.setProperty(ArrowProperty.SKELETON_LOOT_WEIGHT, config.getDouble("Arrow.Death.Skeleton.LootDropWeight", 10.0));
	}

	@Override
	public void tick(AlchemicalArrowEntity arrow, Location location) {
		location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 2, 0.1, 0.1, 0.1, 0.01);
	}

	@Override
	public void onHitPlayer(AlchemicalArrowEntity arrow, Player player) {
		this.attemptInstantDeath(arrow.getArrow(), player);
		player.addPotionEffect(WITHER_EFFECT);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SKELETON_DEATH, 1, 0.5F);
	}

	@Override
	public void onHitEntity(AlchemicalArrowEntity arrow, Entity entity) {
		if (!(entity instanceof LivingEntity)) return;
		LivingEntity lEntity = (LivingEntity) entity;

		this.attemptInstantDeath(arrow.getArrow(), lEntity);
		lEntity.addPotionEffect(WITHER_EFFECT);
		lEntity.getWorld().playSound(lEntity.getLocation(), Sound.ENTITY_WITHER_SKELETON_DEATH, 1, 0.5F);
	}

	private void attemptInstantDeath(Arrow source, LivingEntity entity) {
		if (!config.getBoolean("Arrow.Death.Effect.InstantDeathPossible", true)) return;

		int chance = RANDOM.nextInt(100);
		if (chance > config.getDouble("Arrow.Death.Effect.InstantDeathChance", 20.0)) return;

		entity.damage(entity.getHealth(), source);
	}

}