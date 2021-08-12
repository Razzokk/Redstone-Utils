package rzk.redstoneutils.registry;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import rzk.redstoneutils.RedstoneUtils;
import rzk.redstoneutils.item.ItemBlockRotator;

import java.util.ArrayList;
import java.util.List;

public final class ModItems
{
    private static final List<Item> ITEMS = new ArrayList<>();

    public static Item rotator;

    private ModItems() {}

    private static void initItems()
    {
        rotator = registerItem("rotator", new ItemBlockRotator());
    }

    public static Item registerItem(String name, Item item)
    {
        item.setRegistryName(RedstoneUtils.MOD_ID, name);
        ITEMS.add(item);
        return item;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        initItems();
        IForgeRegistry<Item> registry = event.getRegistry();
        ITEMS.forEach(registry::register);
    }

    public static Item.Properties defaultItemProps()
    {
        return new Item.Properties().tab(RedstoneUtils.ITEM_GROUP);
    }
}
