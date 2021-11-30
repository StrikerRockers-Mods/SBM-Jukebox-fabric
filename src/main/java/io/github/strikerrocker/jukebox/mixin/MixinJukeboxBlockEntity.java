package io.github.strikerrocker.jukebox.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(JukeboxBlockEntity.class)
public class MixinJukeboxBlockEntity implements Container {
    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return instance().getRecord().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return instance().getRecord();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = instance().getRecord();
        instance().clearContent();
        setPlayState(instance(), false);
        instance().getLevel().levelEvent(1010, instance().getBlockPos(), Item.getId(Items.AIR));
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = instance().getRecord();
        instance().clearContent();
        setPlayState(instance(), false);
        instance().getLevel().levelEvent(1010, instance().getBlockPos(), Item.getId(Items.AIR));
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (stack.getItem() instanceof RecordItem && isEmpty()) {
            instance().setRecord(stack);
            setPlayState(instance(), true);
            instance().getLevel().levelEvent(1010, instance().getBlockPos(), Item.getId(stack.getItem()));
        }
    }

    @Override
    public void setChanged() {
        Level world = instance().getLevel();
        BlockPos pos = instance().getBlockPos();
        BlockState state = instance().getBlockState();
        if (world != null) {
            world.blockEntityChanged(pos);
            if (!state.isAir()) {
                world.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
    }


    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        instance().setRecord(ItemStack.EMPTY);
    }

    public JukeboxBlockEntity instance() {
        return ((JukeboxBlockEntity) (Object) this);
    }

    private void setPlayState(JukeboxBlockEntity jukebox, boolean b) {
        BlockState state = jukebox.getLevel().getBlockState(jukebox.getBlockPos());
        if (state.getBlock() instanceof JukeboxBlock) {
            jukebox.getLevel().setBlock(jukebox.getBlockPos(), state.setValue(JukeboxBlock.HAS_RECORD, b), 1);
        }
    }
}
