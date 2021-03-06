package rzk.redstoneutils.generators;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import rzk.redstoneutils.RedstoneUtils;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(new BlockStates(generator, RedstoneUtils.MOD_ID, existingFileHelper));
        generator.addProvider(new ItemModels(generator, RedstoneUtils.MOD_ID, existingFileHelper));
        generator.addProvider(new Recipes(generator));
        generator.addProvider(new LootTables(generator));
    }
}
