package rzk.redstoneutils.generators;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import rzk.redstoneutils.registry.ModBlocks;

public final class BlockStates extends BlockStateProvider
{
    public BlockStates(DataGenerator gen, String modid, ExistingFileHelper existingFileHelper)
    {
        super(gen, modid, existingFileHelper);
    }

    private String name(Block block)
    {
        return block.getRegistryName().getPath();
    }

    @Override
    protected void registerStatesAndModels()
    {
        analogLamp(ModBlocks.analogLamp, false);
        analogLamp(ModBlocks.analogLampInverted, true);
        redstoneEmitter(ModBlocks.redstoneEmitter, false);
        redstoneEmitter(ModBlocks.redstoneEmitterInverted, true);
    }

    private void analogLamp(Block block, boolean inverted)
    {
        String name = name(block);
        ResourceLocation on = inverted ? modLoc("blocks/analog_lamp_on") : modLoc("blocks/analog_lamp_off");
        ResourceLocation off = inverted ? modLoc("blocks/analog_lamp_off") : modLoc("blocks/analog_lamp_on");

        getVariantBuilder(block).forAllStates(state ->
        {
            boolean powered = state.getValue(BlockStateProperties.POWER) > 0;


            return ConfiguredModel.builder()
                    .modelFile(models().cubeAll(name + "_" + (powered ? "on" : "off"), powered ? on : off))
                    .build();
        });
        simpleBlockItem(block, models().getExistingFile(modLoc(name + "_off")));
    }

    private void redstoneEmitter(Block block, boolean inverted)
    {
        String name = name(block);
        ResourceLocation sideOn = inverted ? modLoc("blocks/emitter_side_off") : modLoc("blocks/emitter_side_on");
        ResourceLocation sideOff = inverted ? modLoc("blocks/emitter_side_on") : modLoc("blocks/emitter_side_off");
        ResourceLocation topOn = inverted ? modLoc("blocks/emitter_top_off") : modLoc("blocks/emitter_top_on");
        ResourceLocation topOff = inverted ? modLoc("blocks/emitter_top_on") : modLoc("blocks/emitter_top_off");

        directionalBlock(block, state ->
        {
            boolean powered = state.getValue(BlockStateProperties.POWERED);

            return models().cubeTop(name + "_" + (powered ? "on" : "off"),
                    powered ? sideOn : sideOff,
                    powered ? topOn : topOff);
        });
        simpleBlockItem(block, models().getExistingFile(modLoc(name + "_off")));
    }
}
