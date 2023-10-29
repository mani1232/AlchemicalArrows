package wtf.choco.arrows.util;

import com.google.common.base.Preconditions;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import wtf.choco.arrows.AlchemicalArrows;
import wtf.choco.arrows.api.AlchemicalArrowEntity;
import wtf.choco.arrows.registry.ArrowStateManager;

import java.util.ArrayList;
import java.util.List;

public final class ArrowUpdateTask implements Runnable {

    private static ArrowUpdateTask instance = null;
    private static ScheduledTask task;
    private static AlchemicalArrows thisPlugin;

    private final List<AlchemicalArrowEntity> purgeBuffer = new ArrayList<>(16);
    private final ArrowStateManager stateManager;

    private ArrowUpdateTask(@NotNull ArrowStateManager stateManager) {
        this.stateManager = stateManager;
    }

    @NotNull
    public static ArrowUpdateTask startArrowUpdateTask(@NotNull AlchemicalArrows plugin) {
        Preconditions.checkNotNull(plugin, "Cannot pass null instance of plugin");
        thisPlugin = plugin;

        if (instance == null) {
            instance = new ArrowUpdateTask(plugin.getArrowStateManager());
            plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> {
                task = scheduledTask;
                instance.run();
            }, 1, 1);
        }

        return instance;
    }

    public static void cancel() {
        task.cancel();
    }

    @Override
    public void run() {
        for (AlchemicalArrowEntity arrow : stateManager.getArrows()) {
            AbstractArrow bukkitArrow = arrow.getArrow();
            thisPlugin.getServer().getRegionScheduler().execute(thisPlugin, bukkitArrow.getLocation(), () -> {
                if (!bukkitArrow.isValid()) {
                    this.purgeBuffer.add(arrow);
                }

                arrow.getImplementation().tick(arrow, bukkitArrow.getLocation());
            });
        }

        if (purgeBuffer.size() >= 1) {
            this.purgeBuffer.forEach(stateManager::remove);
            this.purgeBuffer.clear();
        }
    }


}
