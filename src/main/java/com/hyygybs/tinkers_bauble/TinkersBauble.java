package com.hyygybs.tinkers_bauble;

import com.hyygybs.tinkers_bauble.modifiers.ModTraits;
import com.hyygybs.tinkers_bauble.stats.ChainMaterialStats;
import com.hyygybs.tinkers_bauble.stats.GemMaterialStats;
import com.hyygybs.tinkers_bauble.stats.RingMaterialStats;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

@Mod(TinkersBauble.MODID)
public class TinkersBauble {
    public static final String MODID = "tinkers_bauble";

    public TinkersBauble(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModItems.register(modBus);
        ModTraits.register(modBus);
        modBus.addListener(this::commonSetup);
    }

    /** 材料注册表由 TConstruct 初始化，必须等它完成后再注册自定义属性类型 */
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            IMaterialRegistry registry = MaterialRegistry.getInstance();
            registry.registerStatType(RingMaterialStats.TYPE);
            registry.registerStatType(ChainMaterialStats.TYPE);
            registry.registerStatType(GemMaterialStats.TYPE);
        });
    }
}
