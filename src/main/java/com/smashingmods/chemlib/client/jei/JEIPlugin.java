package com.smashingmods.chemlib.client.jei;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ChemLib.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration pRegistration) {
        ItemRegistry.getElements().forEach(element -> pRegistration.addIngredientInfo(new ItemStack(element), VanillaTypes.ITEM_STACK, MutableComponent.create(new TranslatableContents("chemlib.jei.element.description"))));
        ItemRegistry.getCompounds().forEach(compound -> pRegistration.addIngredientInfo(new ItemStack(compound), VanillaTypes.ITEM_STACK, MutableComponent.create(new TranslatableContents(String.format("chemlib.jei.compound.%s.description", compound.getChemicalName())))));
    }
}
