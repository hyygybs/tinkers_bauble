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

/** 指环部件的材料属性：攻击力与弹射物力量 */
public class RingMaterialStats implements IMaterialStats.ScaledTooltip {
    public static final MaterialStatsId ID = new MaterialStatsId(TinkersBauble.MODID, "ring");
    public static final MaterialStatType<RingMaterialStats> TYPE = new MaterialStatType<>(ID,
            new RingMaterialStats(0f, 0f),
            RecordLoadable.create(
                    FloatLoadable.ANY.defaultField("attack_damage", 0f, true, RingMaterialStats::attackDamage),
                    FloatLoadable.ANY.defaultField("projectile_damage", 0f, true, RingMaterialStats::projectileDamage),
                    RingMaterialStats::new));

    private final float attackDamage;
    private final float projectileDamage;

    public RingMaterialStats(float attackDamage, float projectileDamage) {
        this.attackDamage = attackDamage;
        this.projectileDamage = projectileDamage;
    }

    public float attackDamage() {
        return attackDamage;
    }

    public float projectileDamage() {
        return projectileDamage;
    }

    @Override
    public MaterialStatType<?> getType() {
        return TYPE;
    }

    @Override
    public List<Component> getLocalizedInfo(float scale) {
        return List.of(
                ToolStats.ATTACK_DAMAGE.formatValue(attackDamage * scale),
                ToolStats.PROJECTILE_DAMAGE.formatValue(projectileDamage * scale));
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return List.of(Component.translatable("stat.tinkers_bauble.ring.description"));
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        ToolStats.ATTACK_DAMAGE.update(builder, attackDamage * scale);
        ToolStats.PROJECTILE_DAMAGE.update(builder, projectileDamage * scale);
    }
}
