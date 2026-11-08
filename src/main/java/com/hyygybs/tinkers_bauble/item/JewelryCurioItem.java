package com.hyygybs.tinkers_bauble.item;

import com.google.common.collect.Multimap;
import com.ssakura49.tinkercuriolib.tools.item.ModifiableCurioItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.UUID;


public class JewelryCurioItem extends ModifiableCurioItem {
    public JewelryCurioItem(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    @Override
    protected void getCurioStatAttributes(Multimap<Attribute, AttributeModifier> attributes, ToolStack tool, UUID uuid) {
        addAttribute(attributes, uuid, "armor", Attributes.ARMOR, tool.getStats().get(ToolStats.ARMOR));
        addAttribute(attributes, uuid, "armor_toughness", Attributes.ARMOR_TOUGHNESS, tool.getStats().get(ToolStats.ARMOR_TOUGHNESS));
        addAttribute(attributes, uuid, "attack_damage", Attributes.ATTACK_DAMAGE, tool.getStats().get(ToolStats.ATTACK_DAMAGE));
    }

    private static void addAttribute(Multimap<Attribute, AttributeModifier> attributes, UUID uuid, String name,
                                     Attribute attribute, float amount) {
        if (amount > 0) {
            attributes.put(attribute, new AttributeModifier(uuid, "tinkers_bauble." + name, amount, AttributeModifier.Operation.ADDITION));
        }
    }

    /** 饰品没有耐久，不接受任何损伤 */
    @Override
    public boolean canBeDepleted() {
        return false;
    }

    /** 不显示耐久条 */
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public List<Component> getStatInformation(IToolStackView tool, Player player, List<Component> tooltips, TooltipKey key, TooltipFlag flag) {
        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
        builder.addOptional(ToolStats.ARMOR);
        builder.addOptional(ToolStats.ARMOR_TOUGHNESS);
        builder.addOptional(ToolStats.ATTACK_DAMAGE);
        builder.addOptional(ToolStats.PROJECTILE_DAMAGE);
        builder.addAllFreeSlots();
        for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltips, key, flag);
        }
        return tooltips;
    }
}
