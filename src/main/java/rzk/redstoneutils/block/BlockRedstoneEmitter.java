package rzk.redstoneutils.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import rzk.redstoneutils.misc.RUConstants;

import javax.annotation.Nullable;

import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.FACING;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;

public class BlockRedstoneEmitter extends BlockRedstoneDevice
{
    private final static IntegerProperty EMIT_POWER = IntegerProperty.create("emit_power", 1, 15);
    private final boolean inverted;

    public BlockRedstoneEmitter(boolean inverted)
    {
        super(Properties.of(Material.METAL));
        this.inverted = inverted;
        registerDefaultState(defaultBlockState().setValue(EMIT_POWER, 1).setValue(FACING, Direction.NORTH));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
    {
        if (!world.isClientSide)
        {
            int newEmitPower = state.getValue(EMIT_POWER) + 1;

            if (newEmitPower > RUConstants.MAX_REDSTONE_POWER)
                newEmitPower = 1;

            world.setBlock(pos, state.setValue(EMIT_POWER, newEmitPower), Constants.BlockFlags.BLOCK_UPDATE);
            updateNeighbours(state, world, pos);
        }

        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction facing = context.getNearestLookingDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing).setValue(POWERED, getInputPower(context.getLevel(), context.getClickedPos(), facing) > 0);
    }

    @Override
    public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side)
    {
        if (state.getValue(FACING).getOpposite() == side)
            return 0;

        int onPower = inverted ? 0 : state.getValue(EMIT_POWER);
        int offPower = inverted ? state.getValue(EMIT_POWER) : 0;
        return state.getValue(POWERED) ? onPower : offPower;
    }

    @Override
    protected void onInputChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbour, Direction side)
    {
        if (!world.isClientSide && shouldUpdate(state, world, pos))
            world.getBlockTicks().scheduleTick(pos, this, RUConstants.REDSTONE_TICK);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        setPowered(state, world, pos, isGettingPowered(state, world, pos));
    }

    @Override
    public boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    protected boolean isInputSide(BlockState state, Direction side)
    {
        return state.getValue(FACING) == side;
    }

    @Override
    protected boolean isOutputSide(BlockState state, Direction side)
    {
        return !isInputSide(state, side);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(EMIT_POWER, FACING);
    }
}
