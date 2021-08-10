package rzk.redstoneutils.misc;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import rzk.redstoneutils.RedstoneUtils;

public final class RUItemGroup extends ItemGroup
{
    public RUItemGroup()
    {
        super(RedstoneUtils.MOD_ID);
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(Items.REDSTONE);
    }
}
