package al132.chemlib.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DankFoodHandler implements IRecipe {
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 2;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation("chemlib","crafting_dank");
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public IRecipeType<?> getType() {
        return null;
    }
}
