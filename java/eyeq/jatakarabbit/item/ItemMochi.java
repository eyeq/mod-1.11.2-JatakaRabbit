package eyeq.jatakarabbit.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemMochi extends ItemFood {
    public ItemMochi(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity) {
        entity.attackEntityFrom(DamageSource.IN_WALL, 5);
        if(entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).getFoodStats().addStats(this, itemStack);
        }
        if(!player.isCreative()) {
            itemStack.shrink(1);
        }
        return super.onLeftClickEntity(itemStack, player, entity);
    }
}
