package al132.chemlib;

import al132.chemlib.blocks.ModBlocks;
import al132.chemlib.capability.CapabilityDrugInfo;
import al132.chemlib.capability.ChemLibDrugDispatcher;
import al132.chemlib.items.BaseItem;
import al132.chemlib.items.DankMolecule;
import al132.chemlib.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("chemlib")
public class ChemLib {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "chemlib";

    public static final TextFormatting CHEM_TOOLTIP_COLOR = TextFormatting.DARK_AQUA;

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.hydrogen);
        }
    };

    public ChemLib() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent e) {
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof PlayerEntity) {
            e.getObject().getCapability(CapabilityDrugInfo.DRUG_INFO).ifPresent(cap ->
                    e.addCapability(new ResourceLocation(MODID, "DrugInfo"), new ChemLibDrugDispatcher()));
        }
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
            ModItems.parseCompounds().stream().forEachOrdered(e.getRegistry()::register);
            ModBlocks.blocks.stream().forEachOrdered(block ->
                    e.getRegistry().register(new BlockItem(block, new Item.Properties().group(ITEM_GROUP))
                            .setRegistryName(block.getRegistryName())));

            DankMolecule.init();
        }

        @SubscribeEvent
        public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> e) {
           // e.getRegistry().register();
        }
    }
}