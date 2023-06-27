package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;

@Mod.EventBusSubscriber(modid = ChemLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabsRegistry {

    public static final DeferredRegister<CreativeModeTab> REGISTRY_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ChemLib.MODID);

    public static RegistryObject<CreativeModeTab> ELEMENT_TAB;
    public static RegistryObject<CreativeModeTab> COMPOUND_TAB;
    public static RegistryObject<CreativeModeTab> METALS_TAB;
    public static RegistryObject<CreativeModeTab> MISC_TAB;

    public static void register(IEventBus pEventBus) {

        ELEMENT_TAB = REGISTRY_TABS.register("element_tab", () -> CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .icon(() -> ItemRegistry.getElementByName("hydrogen")
                        .map(ItemStack::new)
                        .orElseGet(() -> new ItemStack(Items.AIR)))
                .title(Component.translatable("itemGroup.chemlib.elements"))
                .displayItems((pParameters, pOutput) -> ItemRegistry.getElements().forEach(pOutput::accept))
                .build());

        COMPOUND_TAB = REGISTRY_TABS.register("compound_tab", () -> CreativeModeTab.builder()
                .withTabsBefore(ELEMENT_TAB.getKey())
                .icon(() -> ItemRegistry.getCompoundByName("cobalt_aluminate").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR)))
                .title(Component.translatable("itemGroup.chemlib.compounds"))
                .displayItems((pParameters, pOutput) -> {
                    ItemRegistry.REGISTRY_COMPOUNDS.getEntries().stream()
                            .map(RegistryObject::get)
                            .map(item -> (CompoundItem) item)
                            .sorted(Comparator.comparing(CompoundItem::getChemicalName))
                            .toList()
                            .forEach(pOutput::accept);
                    ItemRegistry.getChemicalItemsByTypeAsStream(ChemicalItemType.COMPOUND)
                            .sorted(Comparator.comparing(ChemicalItem::getChemicalName))
                            .toList()
                            .forEach(pOutput::accept);
                })
                .build());

        METALS_TAB = REGISTRY_TABS.register("metals_tab", () -> CreativeModeTab.builder()
                .withTabsBefore(COMPOUND_TAB.getKey())
                .icon(() -> ItemRegistry.getChemicalItemByNameAndType("barium", ChemicalItemType.INGOT).map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR)))
                .title(Component.translatable("itemGroup.chemlib.metals"))
                .displayItems((pParameters, pOutput) -> {
                    ItemRegistry.getChemicalItemsByType(ChemicalItemType.INGOT).stream().map(ItemStack::new).forEach(pOutput::accept);
                    ItemRegistry.getChemicalBlockItems().stream().filter(item -> ((ChemicalBlock) item.getBlock()).getBlockType().getSerializedName().equals("metal")).map(ItemStack::new).forEach(pOutput::accept);
                    ItemRegistry.getChemicalItemsByType(ChemicalItemType.NUGGET).stream().map(ItemStack::new).forEach(pOutput::accept);
                    ItemRegistry.getChemicalItemsByType(ChemicalItemType.DUST).stream().map(ItemStack::new).forEach(pOutput::accept);
                    ItemRegistry.getChemicalItemsByType(ChemicalItemType.PLATE)
                            .stream()
                            .filter(chemicalItem -> !chemicalItem.getDescriptionId().equals("item.chemlib.polyvinyl_chloride_plate"))
                            .map(ItemStack::new).forEach(pOutput::accept);
                })
                .build());

        MISC_TAB = REGISTRY_TABS.register("misc_tab", () -> CreativeModeTab.builder()
                .withTabsBefore(METALS_TAB.getKey())
                .icon(() -> ItemRegistry.getChemicalBlockItemByName("radon_lamp_block").map(ItemStack::new).orElseGet(() -> new ItemStack(Items.AIR)))
                .title(Component.translatable("itemGroup.chemlib.misc"))
                .displayItems((pParameters, pOutput) -> {
                    ItemRegistry.REGISTRY_BLOCK_ITEMS.getEntries().stream().map(RegistryObject::get).filter(item -> item.getDescriptionId().contains("lamp_block")).forEach(pOutput::accept);
                    ItemRegistry.REGISTRY_MISC_ITEMS.getEntries().stream().map(RegistryObject::get).forEach(pOutput::accept);
                    ItemRegistry.getChemicalItemByNameAndType("polyvinyl_chloride", ChemicalItemType.PLATE).ifPresent(pOutput::accept);
                    FluidRegistry.getAllSortedBuckets().stream().map(ItemStack::new).forEach(pOutput::accept);
                })
                .build());

        REGISTRY_TABS.register(pEventBus);
    }
}
