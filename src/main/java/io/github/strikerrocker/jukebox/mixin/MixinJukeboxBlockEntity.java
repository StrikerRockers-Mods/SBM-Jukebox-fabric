package io.github.strikerrocker.jukebox.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(JukeboxBlockEntity.class)
public class MixinJukeboxBlockEntity implements Inventory {
    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return instance().getRecord().isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return instance().getRecord();
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = instance().getRecord();
        instance().clear();
        setPlayState(instance(), false);
        instance().getWorld().syncWorldEvent(WorldEvents.MUSIC_DISC_PLAYED, instance().getPos(), Item.getRawId(Items.AIR));
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = instance().getRecord();
        instance().clear();
        setPlayState(instance(), false);
        instance().getWorld().syncWorldEvent(WorldEvents.MUSIC_DISC_PLAYED, instance().getPos(), Item.getRawId(Items.AIR));
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (stack.getItem() instanceof MusicDiscItem && isEmpty()) {
            instance().setRecord(stack);
            setPlayState(instance(), true);
            instance().getWorld().syncWorldEvent(1010, instance().getPos(), Item.getRawId(stack.getItem()));
        }
    }

    @Override
    public void markDirty() {
        World world = instance().getWorld();
        BlockPos pos = instance().getPos();
        BlockState state = instance().getCachedState();
        if (world != null) {
            world.markDirty(pos);
            if (!state.isAir()) {
                world.updateComparators(pos, state.getBlock());
            }
        }
    }


    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        instance().setRecord(ItemStack.EMPTY);
    }

    public JukeboxBlockEntity instance() {
        return ((JukeboxBlockEntity) (Object) this);
    }

    private void setPlayState(JukeboxBlockEntity jukebox, boolean b) {
        BlockState state = jukebox.getWorld().getBlockState(jukebox.getPos());
        if (state.getBlock() instanceof JukeboxBlock) {
            jukebox.getWorld().setBlockState(jukebox.getPos(), state.with(JukeboxBlock.HAS_RECORD, b), 1);
        }
    }
}
