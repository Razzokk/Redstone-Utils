package rzk.redstoneutils.generators;

import net.minecraft.data.DataGenerator;
import rzk.redstoneutils.registry.ModBlocks;

public final class LootTables extends BaseLootTableProvider
{
    public LootTables(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void addTables()
    {
        addSimpleDropTable(ModBlocks.analogLamp);
        addSimpleDropTable(ModBlocks.analogLampInverted);
        addSimpleDropTable(ModBlocks.redstoneEmitter);
        addSimpleDropTable(ModBlocks.redstoneEmitterInverted);
    }
}
