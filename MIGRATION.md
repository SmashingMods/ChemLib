# ChemLib — Migration Notes (1.20.1 → 1.21.1)

This release ports ChemLib from Forge 1.20.1 to NeoForge 21.1.228 / Minecraft 1.21.1. The
update is **API-breaking**. The notes below cover every change a downstream mod needs to make.

The mod id (`chemlib`) and Java package roots (`com.smashingmods.chemlib.*`) are unchanged.

---

## `Element.getGroupName()` → `getGroupKey()`

`Element.getGroupName()` is renamed to `getGroupKey()`. The return value is no longer a literal
display string ("Alkali Metals") — it is a translation key ("chemlib.group.alkali_metals").
Consumers must pass it through `Component.translatable(...)` to obtain the localized text.

```java
// before (1.20.1)
Component label = Component.literal(element.getGroupName());

// after (1.21.1)
Component label = Component.translatable(element.getGroupKey()).withStyle(ChatFormatting.GRAY);
```

Empty-string return (atomic numbers outside the periodic table groupings) is preserved — callers
should still guard with `if (!groupKey.isEmpty())`.

---

## Tag namespace `forge:` → `c:`

NeoForge 1.21 adopts the cross-loader **Common** tag namespace (`c:`) also used by Fabric.
ChemLib's block and item tag generators emit tags under `c:` instead of `forge:`. Datapacks,
recipe JSONs, and any code references must be updated.

Affected tag families (generated for every chemical):

- `c:dusts/{chemical}` (was `forge:dusts/{chemical}`)
- `c:storage_blocks/{chemical}` (was `forge:storage_blocks/{chemical}`)

Plus the following special-case dust tags:

- `c:dusts/niter` (was `forge:dusts/niter`)
- `c:dusts/apatite` (was `forge:dusts/apatite`)
- `c:dusts/cinnabar` (was `forge:dusts/cinnabar`)
- `c:sawdust` (was `forge:sawdust`)

Suggested search-and-replace in any consumer:
- `forge:dusts/` → `c:dusts/`
- `forge:storage_blocks/` → `c:storage_blocks/`
- `forge:sawdust` → `c:sawdust`

---

## `ChemicalLiquidBlock` constructor

```java
// before (1.20.1)
public ChemicalLiquidBlock(Supplier<? extends FlowingFluid> pFluid, String pChemicalName)

// after (1.21.1)
public ChemicalLiquidBlock(FlowingFluid pFluid, String pChemicalName)
```

Unwrap the supplier at the call site (`fluidSupplier.get()`).
