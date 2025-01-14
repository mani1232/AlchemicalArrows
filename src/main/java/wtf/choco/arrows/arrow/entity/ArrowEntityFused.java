package wtf.choco.arrows.arrow.entity;

import org.bukkit.entity.Arrow;
import org.jetbrains.annotations.NotNull;
import wtf.choco.arrows.api.AlchemicalArrow;
import wtf.choco.arrows.api.AlchemicalArrowEntity;

public class ArrowEntityFused extends AlchemicalArrowEntity {

    private final int maxFuseTicks;
    private int fuse;

    public ArrowEntityFused(@NotNull AlchemicalArrow type, @NotNull Arrow arrow, int maxFuseTicks) {
        super(type, arrow);
        this.maxFuseTicks = maxFuseTicks;
        this.fuse = 0;
    }

    public int getMaxFuseTicks() {
        return maxFuseTicks;
    }

    public void tickFuse() {
        this.fuse++;
    }

    public int getFuse() {
        return fuse;
    }

    public void setFuse(int fuse) {
        this.fuse = fuse;
    }

    public boolean isFuseFinished() {
        return fuse >= maxFuseTicks;
    }

}
