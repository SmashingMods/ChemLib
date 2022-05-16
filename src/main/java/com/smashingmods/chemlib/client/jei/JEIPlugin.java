package com.smashingmods.chemlib.client.jei;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ChemLib.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration pRegistration) {
        ItemRegistry.getElements().forEach(element -> {
            pRegistration.addIngredientInfo(new ItemStack(element), VanillaTypes.ITEM_STACK, new TextComponent(I18n.get(String.format("%s.jei.element.%s.description", ChemLib.MODID, element.getChemicalName()))));
        });
    }
}
