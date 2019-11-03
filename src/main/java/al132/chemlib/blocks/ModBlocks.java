package al132.chemlib.blocks;

import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

    public static List<Block> blocks = new ArrayList<>();

    public static BlockLamp neonLamp = new BlockLamp("neon");
    public static BlockLamp heliumLamp = new BlockLamp("helium");
    public static BlockLamp argonLamp = new BlockLamp("argon");
    public static BlockLamp kryptonLamp = new BlockLamp("krypton");
    public static BlockLamp xenonLamp = new BlockLamp("xenon");
}