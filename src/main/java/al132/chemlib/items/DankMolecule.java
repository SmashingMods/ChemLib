package al132.chemlib.items;

import al132.chemlib.Utils;
import al132.chemlib.capability.CapabilityDrugInfo;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DankMolecule {

    public Item item;
    public int duration;
    public int amplifier;
    public List<Effect> effects;
    public Consumer<PlayerEntity> entityEffects;

    public static HashMap<Item, DankMolecule> dankMolecules = new HashMap<>();

    public DankMolecule(Item item, int duration, int amplifier, List<Effect> effects, Consumer<PlayerEntity> entityEffects) {
        this.item = item;
        this.duration = duration;
        this.amplifier = amplifier;
        this.effects = effects;
        this.entityEffects = entityEffects;
    }

    public void activateForPlayer(PlayerEntity player) {
        for (Effect effect : this.effects) {
            effects.forEach(x -> player.addEffect(new EffectInstance(effect, duration, amplifier)));
        }
        if (entityEffects != null) entityEffects.accept(player);
    }

    public static void init() {
        Utils.getChemItem("compound_potassium_cyanide").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 500, 2,
                        Lists.newArrayList(Effects.WITHER, Effects.POISON, Effects.CONFUSION, Effects.MOVEMENT_SLOWDOWN, Effects.HUNGER),
                        (player) -> {
                            //player.getFoodStats().setFoodSaturationLevel(0.0f); oops- clientside only
                            player.getFoodData().setFoodLevel(0);
                            player.hurt(DamageSource.STARVE, 16.0f);
                        })));

        Utils.getChemItem("compound_psilocybin").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 600, 2,
                        Lists.newArrayList(Effects.NIGHT_VISION, Effects.GLOWING, Effects.MOVEMENT_SLOWDOWN),
                        (player) -> {
                            player.getCapability(CapabilityDrugInfo.DRUG_INFO).ifPresent(data -> data.psilocybinTicks = 1100);
                        }))); // todo: shaders?
        Utils.getChemItem("compound_penicillin").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 0, 0, Lists.newArrayList(),
                        (player) -> {
                            player.removeAllEffects();
                            player.heal(2.0f);
                        })));
        Utils.getChemItem("compound_epinephrine").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 200, 0,
                        Lists.newArrayList(Effects.JUMP, Effects.MOVEMENT_SPEED, Effects.DIG_SPEED), (p) -> {
                })));
        Utils.getChemItem("compound_cocaine").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 400, 2,
                        Lists.newArrayList(Effects.NIGHT_VISION, Effects.MOVEMENT_SPEED, Effects.DIG_SPEED, Effects.JUMP), (p) -> {
                })));
        Utils.getChemItem("compound_acetylsalicylic_acid").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 0, 0, Lists.newArrayList(),
                        (player) -> player.heal(5.0f))));

        Utils.getChemItem("compound_caffeine").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 400, 0,
                        Lists.newArrayList(Effects.NIGHT_VISION, Effects.MOVEMENT_SPEED, Effects.DIG_SPEED), (p) -> {
                })));
    }


    public static boolean hasDankMolecule(ItemStack stack) {
        return dankMolecules.containsKey(stack.getItem());
    }

    public static Optional<DankMolecule> getDankMolecule(ItemStack stack) {
        return Optional.ofNullable(dankMolecules.get(stack.getItem()));
    }
}