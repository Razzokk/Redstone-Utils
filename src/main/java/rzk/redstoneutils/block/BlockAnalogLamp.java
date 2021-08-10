package rzk.redstoneutils.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rzk.redstoneutils.misc.Constants;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.POWER;

public class BlockAnalogLamp extends Block
{
    private final boolean inverted;

    public BlockAnalogLamp(boolean inverted)
    {
        super(Properties.of(Material.BUILDABLE_GLASS).strength(0.3F).sound(SoundType.GLASS).isValidSpawn((state, world, pos, type) -> true));
        this.inverted = inverted;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getLevel();

        if (!world.isClientSide)
        {
            BlockPos pos = context.getClickedPos();
            int power = world.getBestNeighborSignal(pos);

            if (power > 0)
                return defaultBlockState().setValue(POWER, power);
        }

        return super.getStateForPlacement(context);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbour, BlockPos neighbourPos, boolean isMoving)
    {
        if (!world.isClientSide)
        {
            int power = world.getBestNeighborSignal(pos);

            if (power != state.getValue(POWER))
                world.setBlockAndUpdate(pos, state.setValue(POWER, power));
        }
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
    {
        int power = state.getValue(POWER);
        return inverted ? Constants.MAX_REDSTONE_POWER - power : power;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(POWER);
    }
}
