package al132.chemlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockLamp extends Block {
    public BlockLamp(String name) {
        super(Block.Properties
                .create(Material.EARTH)
                .hardnessAndResistance(2.0f)
                .sound(SoundType.GLASS)
                .setLightLevel((x) -> 15)); //light level
        setRegistryName("lamp_" + name);
        ModBlocks.blocks.add(this);
    }
}
