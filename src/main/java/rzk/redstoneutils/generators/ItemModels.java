package rzk.redstoneutils.generators;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import rzk.redstoneutils.registry.ModItems;

public final class ItemModels extends ItemModelProvider
{
    public ItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
    {
        super(generator, modid, existingFileHelper);
    }

    private String name(Item item)
    {
        return item.getRegistryName().getPath();
    }

    @Override
    protected void registerModels()
    {
        simpleItem(ModItems.rotator);
    }

    private void simpleItem(Item item, ResourceLocation texture)
    {

        singleTexture(name(item), mcLoc("item/generated"), "layer0", texture);
    }

    private void simpleItem(Item item)
    {
        simpleItem(item, modLoc("items/" + name(item)));
    }
}
