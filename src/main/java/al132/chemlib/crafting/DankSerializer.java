package al132.chemlib.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class DankSerializer implements IRecipeSerializer {
    @Override
    public IRecipe<?> read(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Override
    public IRecipe<?> read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, IRecipe recipe) {

    }

    @Override
    public Object setRegistryName(ResourceLocation name) {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return null;
    }

    @Override
    public Class getRegistryType() {
        return null;
    }
}
