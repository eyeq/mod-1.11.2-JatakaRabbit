package eyeq.jatakarabbit.item;

import eyeq.jatakarabbit.block.BlockMortar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMallet extends ItemSword {
    public ItemMallet() {
        super(ToolMaterial.WOOD);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemStack) {
        World world = entity.getEntityWorld();
        if(world.isRemote) {
            return super.onEntitySwing(entity, itemStack);
        }
        if(itemRand.nextFloat() < 0.01F) {
            BlockPos pos = new BlockPos(entity.getPositionVector().add(entity.getLookVec()));
            IBlockState state = world.getBlockState(pos);
            if(state.getBlock() instanceof BlockMortar) {
                if(((BlockMortar) state.getBlock()).pound(world, pos, state, 1, false)) {
                    itemStack.damageItem(1, entity);
                }
            }
        }
        return super.onEntitySwing(entity, itemStack);
    }

    @Override
    public float getStrVsBlock(ItemStack itemStack, IBlockState state) {
        return 0.0F;
    }
}
