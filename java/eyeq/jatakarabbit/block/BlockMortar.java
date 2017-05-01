package eyeq.jatakarabbit.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import eyeq.jatakarabbit.JatakaRabbit;

public class BlockMortar extends Block {
    protected static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
    protected static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
    protected static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
    protected static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 4);

    public BlockMortar() {
        super(Material.WOOD);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
        int meta = this.getMetaFromState(state);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125 * meta, 1.0));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean pound(World world, BlockPos pos, IBlockState state, int level, boolean isMochi) {
        int meta = this.getMetaFromState(state);
        if((!isMochi && meta == 0) || meta == 4) {
            return false;
        }
        meta += level;
        if(meta > 4) {
            meta = 4;
        }
        world.setBlockState(pos, state.withProperty(LEVEL, meta), 2);
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if(world.isRemote) {
            return;
        }
        if(entity instanceof EntityRabbit) {
            if(this.pound(world, pos, state, ((EntityAgeable) entity).isChild() ? 2 : 4, true)) {
                entity.setDead();
            }
            return;
        }
        int meta = this.getMetaFromState(state);
        if(meta == 0) {
            return;
        }
        float pow = 1.0F / meta;
        entity.motionX *= pow;
        entity.motionZ *= pow;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(world.isRemote) {
            return true;
        }
        int meta = this.getMetaFromState(state);
        if(meta == 0) {
            return true;
        }
        if(player.isSneaking()) {
            world.setBlockState(pos, state.withProperty(LEVEL, meta - 1), 2);
            ItemStack itemStack = new ItemStack(JatakaRabbit.mochi);
            if(!player.inventory.addItemStackToInventory(itemStack)) {
                player.dropItem(itemStack, false);
            }
            return false;
        }
        if(player.canEat(false)) {
            world.setBlockState(pos, state.withProperty(LEVEL, meta - 1), 2);
            ItemFood mochi = (ItemFood) JatakaRabbit.mochi;
            player.getFoodStats().addStats(mochi, new ItemStack(mochi));
            if(world.rand.nextFloat() < 0.01F) {
                player.attackEntityFrom(DamageSource.IN_WALL, 10 * world.rand.nextFloat());
            }
            return false;
        }
        return true;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEVEL);
    }
}
