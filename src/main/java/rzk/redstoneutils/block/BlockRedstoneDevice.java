package rzk.redstoneutils.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.POWERED;

public abstract class BlockRedstoneDevice extends Block
{
    public BlockRedstoneDevice(Properties properties)
    {
        super(properties.isRedstoneConductor((state, world, pos) -> false));
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    protected static Direction getDirectionFromPos(BlockPos origin, BlockPos end)
    {
        return Direction.fromNormal(end.getX() - origin.getX(), end.getY() - origin.getY(), end.getZ() - origin.getZ());
    }

    @Override
    public abstract boolean isSignalSource(BlockState state);

    protected abstract boolean isInputSide(BlockState state, Direction side);

    protected abstract boolean isOutputSide(BlockState state, Direction side);

    protected abstract void onInputChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbour, Direction side);

    protected boolean isPowered(BlockState state)
    {
        return state.hasProperty(POWERED) && state.getValue(POWERED);
    }

    protected int getInputPower(World world, BlockPos pos, Direction side)
    {
        BlockPos neighbour = pos.relative(side);
        int power = world.getSignal(neighbour, side);

        if (power >= 15)
            return power;

        BlockState neighbourState = world.getBlockState(neighbour);
        return Math.max(power, neighbourState.is(Blocks.REDSTONE_WIRE) ? neighbourState.getValue(BlockStateProperties.POWER) : 0);
    }

    protected boolean isGettingPowered(BlockState state, World world, BlockPos pos, Direction... sides)
    {
        if (sides == null || sides.length == 0)
            return isGettingPowered(state, world, pos, Direction.values());

        for (Direction side : sides)
            if (isInputSide(state, side) && getInputPower(world, pos, side) > 0)
                return true;

        return false;
    }

    protected void updateNeighborsInFront(World world, BlockPos pos, Direction side)
    {
        BlockPos neighbour = pos.relative(side);
        world.neighborChanged(neighbour, this, pos);
        // TODO: Maybe only update neighbours of neighbour if something isProvidingStrongPower
        world.updateNeighborsAtExceptFromFacing(neighbour, this, side.getOpposite());
    }

    protected void updateNeighbours(BlockState state, World world, BlockPos pos, Direction... sides)
    {
        if (sides == null || sides.length == 0)
        {
            updateNeighbours(state, world, pos, Direction.values());
            return;
        }

        for (Direction side : sides)
            if (isOutputSide(state, side))
                updateNeighborsInFront(world, pos, side);
    }

    protected void setPowered(BlockState state, World world, BlockPos pos, boolean powered)
    {
        if (state.is(this))
        {
            world.setBlock(pos, state.setValue(POWERED, powered), Constants.BlockFlags.BLOCK_UPDATE);

            if (isSignalSource(state))
                updateNeighbours(state, world, pos);
        }
    }

    protected boolean shouldUpdate(BlockState state, World world, BlockPos pos)
    {
        return isGettingPowered(state, world, pos) != state.getValue(POWERED);
    }

    @Override
    public int getSignal(BlockState state, IBlockReader blockReader, BlockPos pos, Direction side)
    {
        return isPowered(state) && isOutputSide(state, side.getOpposite()) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, IBlockReader blockReader, BlockPos pos, Direction side)
    {
        return getSignal(state, blockReader, pos, side);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side)
    {
        return side != null && (isInputSide(state, side.getOpposite()) || isOutputSide(state, side.getOpposite()));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbour, boolean isMoving)
    {
        Direction side = getDirectionFromPos(pos, neighbour);

        if (isInputSide(state, side))
            onInputChanged(state, world, pos, block, neighbour, side);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(POWERED);
    }
}
