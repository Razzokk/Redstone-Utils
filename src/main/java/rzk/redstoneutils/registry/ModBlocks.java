package rzk.redstoneutils.registry;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import rzk.redstoneutils.RedstoneUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ModBlocks
{
    private static final List<Block> BLOCKS = new ArrayList<>();

    private ModBlocks() {}

    private static void initBlocks()
    {

    }

    private static Block registerBlockNoItem(String name, Block block)
    {
        block.setRegistryName(RedstoneUtils.MOD_ID, name);
        BLOCKS.add(block);

        return block;
    }

    private static Block registerBlock(String name, Block block, Function<Block, BlockItem> itemProvider)
    {
        registerBlockNoItem(name, block);
        ModItems.registerItem(name, itemProvider.apply(block));
        return block;
    }

    private static Block registerBlock(String name, Block block)
    {
        return registerBlock(name, block, blockToItem -> new BlockItem(blockToItem, ModItems.defaultItemProps()));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        initBlocks();
        IForgeRegistry<Block> registry = event.getRegistry();
        BLOCKS.forEach(registry::register);
    }
}
