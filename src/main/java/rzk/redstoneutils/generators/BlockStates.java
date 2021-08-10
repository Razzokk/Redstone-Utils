package rzk.redstoneutils.generators;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import rzk.redstoneutils.registry.ModBlocks;

public class BlockStates extends BlockStateProvider
{
    public BlockStates(DataGenerator gen, String modid, ExistingFileHelper existingFileHelper)
    {
        super(gen, modid, existingFileHelper);
    }

    protected String name(Block block)
    {
        return block.getRegistryName().getPath();
    }

    @Override
    protected void registerStatesAndModels()
    {
        analogLamp(ModBlocks.analogLamp, modLoc("blocks/analog_lamp_on"), modLoc("blocks/analog_lamp_off"));
        analogLamp(ModBlocks.analogLampInverted, modLoc("blocks/analog_lamp_off"), modLoc("blocks/analog_lamp_on"));
    }

    private void analogLamp(Block block, ResourceLocation onTexture, ResourceLocation offTexture)
    {
        String name = name(block);

        getVariantBuilder(block).forAllStates(state ->
        {
            boolean powered = state.getValue(BlockStateProperties.POWER) > 0;

            return ConfiguredModel.builder()
                    .modelFile(models().cubeAll(name + "_" + (powered ? "on" : "off"), powered ? onTexture : offTexture))
                    .build();
        });

        simpleBlockItem(block, models().getExistingFile(modLoc(name + "_off")));
    }
}
