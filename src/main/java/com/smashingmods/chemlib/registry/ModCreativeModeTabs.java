package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ChemLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {

    public static CreativeModeTab ELEMENT_TAB;
    public static CreativeModeTab COMPOUND_TAB;
    public static CreativeModeTab METALS_TAB;
    public static CreativeModeTab MISC_TAB;

    @SubscribeEvent
    public static void registerCreateModeTabs(CreativeModeTabEvent.Register pEvent) {
        ELEMENT_TAB = pEvent.registerCreativeModeTab(new ResourceLocation(ChemLib.MODID, "element_tab"), List.of(), List.of(CreativeModeTabs.SPAWN_EGGS), builder -> {
            builder.icon(() -> ItemRegistry.getElementByName("hydrogen").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR)))
                    .title(Component.translatable("itemGroup.chemlib.elements"));
        });

        COMPOUND_TAB = pEvent.registerCreativeModeTab(new ResourceLocation(ChemLib.MODID, "compound_tab"), List.of(), List.of(ELEMENT_TAB), builder -> {
            builder.icon(() -> ItemRegistry.getCompoundByName("cobalt_aluminate").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR)))
                    .title(Component.translatable("itemGroup.chemlib.compounds"));
        });

        METALS_TAB = pEvent.registerCreativeModeTab(new ResourceLocation(ChemLib.MODID, "metals_tab"), List.of(), List.of(COMPOUND_TAB), builder -> {
            builder.icon(() -> ItemRegistry.getChemicalItemByNameAndType("barium", ChemicalItemType.INGOT).map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR)))
                    .title(Component.translatable("itemGroup.chemlib.metals"));
        });

        MISC_TAB = pEvent.registerCreativeModeTab(new ResourceLocation(ChemLib.MODID, "misc_tab"), List.of(), List.of(METALS_TAB), builder -> {
            builder.icon(() -> ItemRegistry.getChemicalBlockItemByName("radon_lamp_block").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR)))
                    .title(Component.translatable("itemGroup.chemlib.misc"));
        });
    }

    @SubscribeEvent
    public static void addCreativeModeTabs(CreativeModeTabEvent.BuildContents pEvent) {
        if (pEvent.getTab() == ELEMENT_TAB) {
            ItemRegistry.getElements().forEach(pEvent::accept);
        }
        if (pEvent.getTab() == COMPOUND_TAB) {
            ItemRegistry.REGISTRY_COMPOUNDS.getEntries().stream()
                    .map(RegistryObject::get)
                    .map(item -> (CompoundItem) item)
                    .sorted(Comparator.comparing(CompoundItem::getChemicalName))
                    .collect(Collectors.toList())
                    .forEach(pEvent::accept);
            ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.COMPOUND)
                    .sorted(Comparator.comparing(ChemicalItem::getChemicalName))
                    .collect(Collectors.toList())
                    .forEach(pEvent::accept);
        }
        if (pEvent.getTab() == METALS_TAB) {
            ItemRegistry.getChemicalItemsByType(ChemicalItemType.INGOT).stream().map(ItemStack::new).forEach(pEvent::accept);
            ItemRegistry.getChemicalBlockItems().stream().filter(item -> ((ChemicalBlock) item.getBlock()).getBlockType().getSerializedName().equals("metal")).map(ItemStack::new).forEach(pEvent::accept);
            ItemRegistry.getChemicalItemsByType(ChemicalItemType.NUGGET).stream().map(ItemStack::new).forEach(pEvent::accept);
            ItemRegistry.getChemicalItemsByType(ChemicalItemType.DUST).stream().map(ItemStack::new).forEach(pEvent::accept);
            ItemRegistry.getChemicalItemsByType(ChemicalItemType.PLATE)
                    .stream()
                    .filter(chemicalItem -> !chemicalItem.getDescriptionId().equals("item.chemlib.polyvinyl_chloride_plate"))
                    .map(ItemStack::new).forEach(pEvent::accept);
        }
        if (pEvent.getTab() == MISC_TAB) {
            ItemRegistry.REGISTRY_BLOCK_ITEMS.getEntries().stream().map(RegistryObject::get).filter(item -> item.getDescriptionId().contains("lamp_block")).forEach(pEvent::accept);
            ItemRegistry.REGISTRY_MISC_ITEMS.getEntries().forEach(pEvent::accept);
            ItemRegistry.getChemicalItemByNameAndType("polyvinyl_chloride", ChemicalItemType.PLATE).ifPresent(pEvent::accept);
            FluidRegistry.getAllSortedBuckets().stream().map(ItemStack::new).forEach(pEvent::accept);
        }
    }
}
