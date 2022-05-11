package com.smashingmods.chemlib.items;

import com.smashingmods.chemlib.Utils;

import com.smashingmods.chemlib.capability.CapabilityDrugInfo;
import com.google.common.collect.Lists;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DankMolecule {

    public Item item;
    public int duration;
    public int amplifier;
    public List<MobEffect> effects;
    public Consumer<Player> entityEffects;

    public static HashMap<Item, DankMolecule> dankMolecules = new HashMap<>();

    public DankMolecule(Item item, int duration, int amplifier, List<MobEffect> effects, Consumer<Player> entityEffects) {
        this.item = item;
        this.duration = duration;
        this.amplifier = amplifier;
        this.effects = effects;
        this.entityEffects = entityEffects;
    }

    public void activateForPlayer(Player player) {
        for (MobEffect effect : this.effects) {
            effects.forEach(x -> player.addEffect(new MobEffectInstance(effect, duration, amplifier)));
        }
        if (entityEffects != null) entityEffects.accept(player);
    }

    public static void init() {
        Utils.getChemItem("compound_potassium_cyanide").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 500, 2,
                        Lists.newArrayList(MobEffects.WITHER, MobEffects.POISON, MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.HUNGER),
                        (player) -> {
                            //player.getFoodStats().setFoodSaturationLevel(0.0f); oops- clientside only
                            player.getFoodData().setFoodLevel(0);
                            player.hurt(DamageSource.STARVE, 16.0f);
                        })));

        Utils.getChemItem("compound_psilocybin").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 600, 2,
                        Lists.newArrayList(MobEffects.NIGHT_VISION, MobEffects.GLOWING, MobEffects.MOVEMENT_SLOWDOWN),
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
                        Lists.newArrayList(MobEffects.JUMP, MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED), (p) -> {
                })));
        Utils.getChemItem("compound_cocaine").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 400, 2,
                        Lists.newArrayList(MobEffects.NIGHT_VISION, MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED, MobEffects.JUMP), (p) -> {
                })));
        Utils.getChemItem("compound_acetylsalicylic_acid").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 0, 0, Lists.newArrayList(),
                        (player) -> player.heal(5.0f))));

        Utils.getChemItem("compound_caffeine").ifPresent(item ->
                dankMolecules.put(item, new DankMolecule(item, 400, 0,
                        Lists.newArrayList(MobEffects.NIGHT_VISION, MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED), (p) -> {
                })));
    }


    public static boolean hasDankMolecule(ItemStack stack) {
        return dankMolecules.containsKey(stack.getItem());
    }

    public static Optional<DankMolecule> getDankMolecule(ItemStack stack) {
        return Optional.ofNullable(dankMolecules.get(stack.getItem()));
    }
}