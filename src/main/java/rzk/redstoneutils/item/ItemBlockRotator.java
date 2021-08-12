package rzk.redstoneutils.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import rzk.redstoneutils.registry.ModItems;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.state.properties.BlockStateProperties.FACING;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class ItemBlockRotator extends Item
{
    public ItemBlockRotator()
    {
        super(ModItems.defaultItemProps().stacksTo(1));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        World world = context.getLevel();

        if (!world.isClientSide)
        {
            BlockPos pos = context.getClickedPos();
            BlockState state = world.getBlockState(pos);
            PlayerEntity player = context.getPlayer();

            boolean inverse = player.isShiftKeyDown();
            boolean axisMode = stack.getOrCreateTag().getBoolean("axis_mode");

            if (state.hasProperty(FACING))
            {
                if (axisMode)
                {

                }
                Direction newFacing = context.getClickedFace();

                if (inverse)
                    newFacing = newFacing.getOpposite();

                world.setBlockAndUpdate(pos, state.setValue(FACING, newFacing));
            }
            else if (state.hasProperty(HORIZONTAL_FACING))
            {
                Direction newHorizontalFacing = state.getValue(HORIZONTAL_FACING);
                newHorizontalFacing = inverse ? newHorizontalFacing.getClockWise() : newHorizontalFacing.getCounterClockWise();
                world.setBlockAndUpdate(pos, state.setValue(HORIZONTAL_FACING, newHorizontalFacing));
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        if (player.isShiftKeyDown())
        {
            ItemStack stack = player.getItemInHand(hand);

            if (!world.isClientSide)
            {
                CompoundNBT nbt = stack.getOrCreateTag();
                boolean axisMode = nbt.getBoolean("axis_mode");
                nbt.putBoolean("axis_mode", !axisMode);
            }

            return ActionResult.consume(stack);
        }

        return super.use(world, player, hand);
    }

    @Override
    public void onCraftedBy(ItemStack stack, World world, PlayerEntity player)
    {
        if (!world.isClientSide)
            stack.getOrCreateTag().putBoolean("axis_mode", false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag)
    {
        boolean axisMode = stack.getOrCreateTag().getBoolean("axis_mode");
        ITextComponent mode = new TranslationTextComponent("tooltip.mode." + (axisMode ? "axis" : "face")).withStyle(TextFormatting.YELLOW);
        list.add(new TranslationTextComponent("tooltip.mode", mode).withStyle(TextFormatting.GRAY));
    }
}
