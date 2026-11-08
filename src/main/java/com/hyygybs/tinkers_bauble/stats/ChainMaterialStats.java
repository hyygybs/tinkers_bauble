package com.hyygybs.tinkers_bauble.stats;

import java.util.List;

import net.minecraft.network.chat.Component;

import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import com.hyygybs.tinkers_bauble.TinkersBauble;

/** 链条部件的材料属性：护甲与护甲韧性 */
public class ChainMaterialStats implements IMaterialStats.ScaledTooltip {
    public static final MaterialStatsId ID = new MaterialStatsId(TinkersBauble.MODID, "chain");
    public static final MaterialStatType<ChainMaterialStats> TYPE = new MaterialStatType<>(ID,
            new ChainMaterialStats(0f, 0f),
            RecordLoadable.create(
                    FloatLoadable.ANY.defaultField("armor", 0f, true, ChainMaterialStats::armor),
                    FloatLoadable.ANY.defaultField("armor_toughness", 0f, true, ChainMaterialStats::armorToughness),
                    ChainMaterialStats::new));

    private final float armor;
    private final float armorToughness;

    public ChainMaterialStats(float armor, float armorToughness) {
        this.armor = armor;
        this.armorToughness = armorToughness;
    }

    public float armor() {
        return armor;
    }

    public float armorToughness() {
        return armorToughness;
    }

    @Override
    public MaterialStatType<?> getType() {
        return TYPE;
    }

    @Override
    public List<Component> getLocalizedInfo(float scale) {
        return List.of(
                ToolStats.ARMOR.formatValue(armor * scale),
                ToolStats.ARMOR_TOUGHNESS.formatValue(armorToughness * scale));
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return List.of(Component.translatable("stat.tinkers_bauble.chain.description"));
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        ToolStats.ARMOR.update(builder, armor * scale);
        ToolStats.ARMOR_TOUGHNESS.update(builder, armorToughness * scale);
    }
}
