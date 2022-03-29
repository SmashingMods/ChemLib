package al132.chemlib.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;

public class DankSerializer implements RecipeSerializer {
    @Override
    public Recipe<?> fromJson(ResourceLocation recipeId, JsonObject json) {
        return null;
    }

    @Override
    public Recipe<?> fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        return null;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, Recipe recipe) {

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
