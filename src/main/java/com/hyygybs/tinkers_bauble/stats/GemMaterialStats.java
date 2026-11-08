package com.hyygybs.tinkers_bauble.stats;

import java.util.List;

import net.minecraft.network.chat.Component;

import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import com.hyygybs.tinkers_bauble.TinkersBauble;

/** 宝石部件的材料属性：无属性 */
public final class GemMaterialStats implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(TinkersBauble.MODID, "gem");
    public static final GemMaterialStats INSTANCE = new GemMaterialStats();
    public static final MaterialStatType<GemMaterialStats> TYPE = MaterialStatType.singleton(ID, INSTANCE);

    private static final List<Component> LOCALIZED = List.of(Component.translatable("tool_stat.tconstruct.extra.no_stats"));
    private static final List<Component> DESCRIPTION = List.of(Component.translatable("stat.tinkers_bauble.gem.description"));

    private GemMaterialStats() {
    }

    @Override
    public MaterialStatType<GemMaterialStats> getType() {
        return TYPE;
    }

    @Override
    public List<Component> getLocalizedInfo() {
        return LOCALIZED;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
    }
}
