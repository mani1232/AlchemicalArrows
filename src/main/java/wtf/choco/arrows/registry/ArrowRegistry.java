package wtf.choco.arrows.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.choco.arrows.AlchemicalArrows;
import wtf.choco.arrows.api.AlchemicalArrow;
import wtf.choco.arrows.persistence.AAPersistentDataTypes;
import wtf.choco.arrows.util.AAConstants;

import java.util.*;

/**
 * Handles the registration of {@link AlchemicalArrow} implementations.
 *
 * @author Parker Hawke - Choco
 */
public final class ArrowRegistry implements Iterable<AlchemicalArrow> {

    public static final Set<Material> BOW_MATERIALS = Sets.immutableEnumSet(Material.BOW, Material.CROSSBOW);

    private static final String NATIVE_ARROW_PACKAGE = "wtf.choco.arrows.arrow";

    private final Map<String, AlchemicalArrow> arrows = new HashMap<>();

    /**
     * Register a custom AlchemicalArrow implementation. All arrows must be registered here
     * in order to be recognised by AlchemicalArrows when shooting custom arrows. The provided
     * instance must have a unique, non-null key ({@link AlchemicalArrow#getKey()}), as well as
     * a unique, non-null ItemStack of type {@link Material#ARROW}.
     *
     * @param arrow the arrow implementation to register
     * @throws IllegalArgumentException if arrow, its key or its ItemStack is null, or if the ItemStack
     *                                  is not of type {@link Material#ARROW}
     * @throws IllegalStateException    if an arrow with the ID has already been registered, or an arrow
     *                                  with the ItemStack has already been registered
     */
    public void register(@NotNull AlchemicalArrow arrow) {
        Preconditions.checkArgument(arrow != null, "Cannot register null arrow");

        // Validate key
        NamespacedKey key = arrow.getKey();
        Preconditions.checkArgument(key != null, "Arrow key must not be null");
        Preconditions.checkState(!arrows.containsKey(key.toString()));

        // Validate item
        ItemStack arrowItem = arrow.getItem();
        Preconditions.checkArgument(arrowItem != null, "AlchemicalArrow#getItem() must not be null");
        Preconditions.checkArgument(Tag.ITEMS_ARROWS.isTagged(arrowItem.getType()), "Result of AlchemicalArrow#getItem() must be an arrow material (arrow, tipped or spectral)");

        for (AlchemicalArrow registeredArrow : arrows.values()) {
            if (registeredArrow.getItem().isSimilar(arrowItem)) {
                throw new IllegalStateException("ItemStack is already being used by class " + registeredArrow.getClass().getName());
            }
        }

        Class<?> arrowClass = arrow.getClass();
        if (!arrowClass.getPackage().getName().startsWith(NATIVE_ARROW_PACKAGE)) {
            AlchemicalArrows.getInstance().getLogger().info("Successfully registered external arrow (" + arrowClass.getName() + ")");
        }

        this.arrows.put(key.toString(), arrow);
    }

    /**
     * Unregister an {@link AlchemicalArrow} implementation from the arrow registry. Note that upon
     * unregistration, the arrow will no longer be recognised when attempting to shoot one.
     *
     * @param arrow the arrow to unregister
     */
    public void unregister(@NotNull AlchemicalArrow arrow) {
        Preconditions.checkArgument(arrow != null, "Cannot unregister null arrow");
        Preconditions.checkArgument(arrow.getKey() != null, "Cannot unregister arrow with null key");

        this.arrows.remove(arrow.getKey().toString());
    }

    /**
     * Unregister an {@link AlchemicalArrow} implementation from the arrow registry based upon its
     * class. Note that upon unregistration, the arrow will no longer be recognised when attempting
     * to shoot one.
     *
     * @param arrowClass the class of the arrow to unregister
     */
    public void unregister(@NotNull Class<? extends AlchemicalArrow> arrowClass) {
        arrows.values().removeIf(alchemicalArrow -> alchemicalArrow.getClass() == arrowClass);
    }

    /**
     * Get the registered instance of the provided AlchemicalArrow class. The registration instance
     * provides various informational methods such as its representing ItemStack as well as a method
     * to construct a new instance of the AlchemicalArrowEntity such that an instance of {@link Arrow}
     * is provided.
     *
     * @param <T>   the alchemical arrow class type
     * @param clazz the class of the AlchemicalArrow whose registration to get
     * @return the registered AlchemicalArrow. null if none
     */
    @Nullable
    public <T extends AlchemicalArrow> T get(@NotNull Class<T> clazz) {
        for (AlchemicalArrow arrow : arrows.values()) {
            if (arrow.getClass() == clazz) {
                return clazz.cast(arrow);
            }
        }

        return null;
    }

    /**
     * Get the registered instance of an AlchemicalArrow represented by the provided ItemStack. The
     * registration instance provides various informational methods such as its representing ItemStack
     * as well as a method to construct a new instance of the AlchemicalArrowEntity such that an instance
     * of {@link Arrow} is provided.
     *
     * @param item the item of the AlchemicalArrow instance to retrieve
     * @return the registered AlchemicalArrow represented by the provided ItemStack. null if none
     */
    @Nullable
    public AlchemicalArrow get(@NotNull ItemStack item) {
        Preconditions.checkArgument(item != null, "arrowItem cannot be null");

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = container.get(AAConstants.KEY_TYPE, AAPersistentDataTypes.NAMESPACED_KEY);
        return key != null ? get(key) : null;
    }

    /**
     * Get the registered instance of an AlchemicalArrow according to its unique namespaced key. The
     * registration instance provides various informational methods such as its representing ItemStack
     * as well as a method to construct a new instance of the AlchemicalArrowEntity such that an instance
     * of {@link Arrow} is provided.
     *
     * @param key the unique key of the AlchemicalArrow instance to retrieve
     * @return the registered AlchemicalArrow represented by the provided ItemStack. null if none
     */
    @Nullable
    public AlchemicalArrow get(@NotNull NamespacedKey key) {
        Preconditions.checkArgument(key != null, "Cannot get arrow from null key");
        return get(key.toString());
    }

    /**
     * Get the registered instance of an {@link AlchemicalArrow} according to its registered id. The
     * registration instance provides various informational methods such as its representing ItemStack
     * as well as a method to construct a new instance of the AlchemicalArrowEntity such that an instance
     * of {@link Arrow} is provided.
     *
     * @param key the registered NamespacedKey of the AlchemicalArrow instance to retrieve
     * @return the registered AlchemicalArrow represented by the provided key. null if none
     */
    @Nullable
    public AlchemicalArrow get(@NotNull String key) {
        for (AlchemicalArrow arrow : arrows.values()) {
            if (arrow.getKey().toString().equals(key)) {
                return arrow;
            }
        }

        return null;
    }

    /**
     * Get the registry for all instances of AlchemicalArrows.
     *
     * @return the arrow registration data
     */
    @NotNull
    public Collection<AlchemicalArrow> getRegisteredArrows() {
        return Collections.unmodifiableCollection(arrows.values());
    }

    /**
     * Clear all registry data. This will remove all data previously registered by AlchemicalArrows
     * and by plugins that have registered their own arrows.
     */
    public void clear() {
        this.arrows.clear();
    }

    @Override
    public Iterator<AlchemicalArrow> iterator() {
        return arrows.values().iterator();
    }

}
