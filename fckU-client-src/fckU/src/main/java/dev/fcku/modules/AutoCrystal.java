package dev.fcku.modules;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class AutoCrystal extends Module {
    public double range = 4.5, targetRange = 8;
    public int delay = 2;
    private int timer;

    public AutoCrystal() { super("AutoCrystal"); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (timer-- > 0) return;
        PlayerEntity target = mc.world.getPlayers().stream()
            .filter(p -> p != mc.player && p.isAlive() && mc.player.distanceTo(p) <= targetRange)
            .min(Comparator.comparingDouble(p -> mc.player.distanceTo(p))).orElse(null);
        if (target == null) return;

        // 1) break any crystal near the target
        for (EndCrystalEntity c : mc.world.getEntitiesByClass(EndCrystalEntity.class,
                target.getBoundingBox().expand(6), e -> mc.player.distanceTo(e) <= range)) {
            mc.interactionManager.attackEntity(mc.player, c);
            mc.player.swingHand(Hand.MAIN_HAND);
            timer = delay;
            return;
        }

        // 2) place a crystal on obsidian/bedrock next to the target
        int slot = findHotbar(mc, Items.END_CRYSTAL);
        if (slot == -1) return;
        BlockPos base = findPlacement(mc, target);
        if (base == null) return;
        int prev = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = slot;
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(base).add(0, 0.5, 0), Direction.UP, base, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = prev;
        timer = delay;
    }

    private BlockPos findPlacement(MinecraftClient mc, PlayerEntity t) {
        BlockPos origin = t.getBlockPos();
        BlockPos best = null; double bestD = Double.MAX_VALUE;
        for (int x = -3; x <= 3; x++) for (int y = -2; y <= 1; y++) for (int z = -3; z <= 3; z++) {
            BlockPos p = origin.add(x, y, z);
            var s = mc.world.getBlockState(p);
            if (!s.isOf(Blocks.OBSIDIAN) && !s.isOf(Blocks.BEDROCK)) continue;
            if (!mc.world.isAir(p.up()) || !mc.world.isAir(p.up(2))) continue;
            if (!mc.world.getEntitiesByClass(net.minecraft.entity.Entity.class, new Box(p.up()), e -> true).isEmpty()) continue;
            double reach = mc.player.getEyePos().distanceTo(Vec3d.ofCenter(p));
            if (reach > range) continue;
            double d = Vec3d.ofCenter(p.up()).distanceTo(t.getPos());
            if (d < bestD) { bestD = d; best = p; }
        }
        return best;
    }

    static int findHotbar(MinecraftClient mc, net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) if (mc.player.getInventory().getStack(i).isOf(item)) return i;
        return -1;
    }
}
