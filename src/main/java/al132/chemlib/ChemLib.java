package al132.chemlib;

import al132.chemlib.blocks.ModBlocks;
import al132.chemlib.items.BaseItem;
import al132.chemlib.items.DankMolecule;
import al132.chemlib.items.ModItems;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.ChatFormatting.DARK_AQUA;

@Mod("chemlib")
public class ChemLib {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "chemlib";

    public static final Style CHEM_TOOLTIP_COLOR = Style.EMPTY.withColor(TextColor.fromLegacyFormat(DARK_AQUA));

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.hydrogen);
        }
    };

    public ChemLib() {
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Events());

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> e) {
            for (Block block : ModBlocks.blocks) {
                e.getRegistry().register(block);
            }
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> e) {
            for (BaseItem item : ModItems.items) {
                e.getRegistry().register(item);
            }
            for(Item item: ModItems.parseCompounds()){
                e.getRegistry().register(item);
            }
            for(Block block: ModBlocks.blocks){
                e.getRegistry().register(new BlockItem(block, new Item.Properties().tab(ITEM_GROUP))
                        .setRegistryName(block.getRegistryName()));
            }
            //ModItems.parseCompounds().stream().forEachOrdered(e.getRegistry()::register);
            //ModBlocks.blocks.stream().forEachOrdered(block ->
            //        e.getRegistry().register(new BlockItem(block, new Item.Properties().group(ITEM_GROUP))
            //                .setRegistryName(block.getRegistryName())));
            DankMolecule.init();
        }
    }
}