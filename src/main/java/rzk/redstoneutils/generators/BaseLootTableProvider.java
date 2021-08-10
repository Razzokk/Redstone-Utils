package rzk.redstoneutils.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.SetContents;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseLootTableProvider extends LootTableProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final Map<Block, LootTable.Builder> lootTables  = new HashMap<>();
    private final DataGenerator generator;

    public BaseLootTableProvider(DataGenerator generator)
    {
        super(generator);
        this.generator = generator;
    }

    protected abstract void addTables();

    protected void addTable(Block block, LootTable.Builder builder)
    {
        lootTables.put(block, builder);
    }

    protected void addSimpleDropTable(Block block)
    {
        addTable(block, createSimpleBlockDrop(block));
    }

    protected void addStandardTable(Block block)
    {
        addTable(block, createStandardTable(block));
    }

    protected LootTable.Builder createStandardTable(String name, Block block)
    {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block)
                        .apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
                        .apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
                                .copy("inv", "BlockEntityTag.inv", CopyNbt.Action.REPLACE))
                        .apply(SetContents.setContents()
                                .withEntry(DynamicLootEntry.dynamicEntry(new ResourceLocation("minecraft", "contents")))));

        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder createSimpleBlockDrop(Block block)
    {
        LootPool.Builder builder = LootPool.lootPool()
                .name(name(block))
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block));

        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder createStandardTable(Block block)
    {
        return createStandardTable(name(block), block);
    }

    @Override
    public void run(DirectoryCache cache)
    {
        addTables();
        Map<ResourceLocation, LootTable> tables = new HashMap<>();

        for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet())
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootParameterSets.BLOCK).build());

        writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables)
    {
        Path outputFolder = generator.getOutputFolder();
        tables.forEach((key, lootTable) ->
        {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try
            {
                IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
            } catch (IOException e)
            {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }

    protected String name(Block block)
    {
        return block.getRegistryName().getPath();
    }

    @Override
    public String getName()
    {
        return "Redstone Utils LootTables";
    }
}
