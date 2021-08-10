package rzk.redstoneutils;

import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rzk.redstoneutils.misc.RUItemGroup;
import rzk.redstoneutils.registry.ModBlocks;
import rzk.redstoneutils.registry.ModItems;

@Mod(RedstoneUtils.MOD_ID)
public final class RedstoneUtils
{
    public static final String MOD_ID = "redstoneutils";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ItemGroup ITEM_GROUP = new RUItemGroup();

    public RedstoneUtils()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        eventBus.register(ModBlocks.class);
        eventBus.register(ModItems.class);
    }

    private void setup(FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM PREINIT");
    }
}
