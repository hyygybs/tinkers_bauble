package com.hyygybs.tinkers_bauble;

import com.hyygybs.tinkers_bauble.item.JewelryCurioItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import com.hyygybs.tinkers_bauble.stats.ChainMaterialStats;
import com.hyygybs.tinkers_bauble.stats.GemMaterialStats;
import com.hyygybs.tinkers_bauble.stats.RingMaterialStats;

public final class ModItems {
    public static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(TinkersBauble.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TinkersBauble.MODID);

    public static final ToolDefinition RING_DEFINITION = ToolDefinition.create(new ResourceLocation(TinkersBauble.MODID, "artificers_ring"));
    public static final ToolDefinition NECKLACE_DEFINITION = ToolDefinition.create(new ResourceLocation(TinkersBauble.MODID, "artificers_necklace"));

    /** 指环部件，决定护甲与耐久 */
    public static final ItemObject<ToolPartItem> PLAIN_RING = ITEMS.register("plain_ring",
            () -> new ToolPartItem(new Item.Properties(), RingMaterialStats.ID));
    /** 链条部件，决定护甲、韧性与耐久 */
    public static final ItemObject<ToolPartItem> CHAIN = ITEMS.register("chain",
            () -> new ToolPartItem(new Item.Properties(), ChainMaterialStats.ID));
    /** 宝石部件，决定攻击力与耐久 */
    public static final ItemObject<ToolPartItem> GEM = ITEMS.register("gem",
            () -> new ToolPartItem(new Item.Properties(), GemMaterialStats.ID));

    public static final CastItemObject PLAIN_RING_CAST = ITEMS.registerCast(PLAIN_RING, new Item.Properties());
    public static final CastItemObject CHAIN_CAST = ITEMS.registerCast(CHAIN, new Item.Properties());

    public static final ItemObject<JewelryCurioItem> ARTIFICERS_RING = ITEMS.register("artificers_ring",
            () -> new JewelryCurioItem(new Item.Properties(), RING_DEFINITION));
    public static final ItemObject<JewelryCurioItem> ARTIFICERS_NECKLACE = ITEMS.register("artificers_necklace",
            () -> new JewelryCurioItem(new Item.Properties(), NECKLACE_DEFINITION));

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + TinkersBauble.MODID))
            .icon(() -> ToolBuildHandler.buildItemRandomMaterials(ARTIFICERS_RING.get(), RandomSource.create()))
            .displayItems((params, output) -> {
                PLAIN_RING.get().addVariants(output::accept, "");
                CHAIN.get().addVariants(output::accept, "");
                GEM.get().addVariants(output::accept, "");

                output.accept(PLAIN_RING_CAST.get());
                output.accept(PLAIN_RING_CAST.getSand());
                output.accept(PLAIN_RING_CAST.getRedSand());
                output.accept(CHAIN_CAST.get());
                output.accept(CHAIN_CAST.getSand());
                output.accept(CHAIN_CAST.getRedSand());

                ToolBuildHandler.addVariants(output::accept, ARTIFICERS_RING.get(), "");
                ToolBuildHandler.addVariants(output::accept, ARTIFICERS_NECKLACE.get(), "");
            }).build());

    private ModItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        CREATIVE_TABS.register(modBus);
    }
}
