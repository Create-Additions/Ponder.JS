package com.kotakotik.pondermaker.kubejs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.antlr.v4.runtime.misc.Triple;

import java.util.ArrayList;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PonderRegistryEventJS extends EventJS {
    public static ArrayList<PonderBuilderJS> ALL = new ArrayList<>();

    public PonderBuilderJS create(String name, ResourceLocation... items) {
        PonderBuilderJS b = new PonderBuilderJS(name, items);
        ALL.add(b);
        return b;
    }

    public void register(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            PonderRegistry.startRegistration("kubejs");
            for(PonderBuilderJS b : ALL) b.execute();
//                PonderRegistry.forComponents(itemProvider)
//                        .addStoryBoard("test", b.function::accept);
//                PonderRegistry.TAGS.forTag(PonderTag.KINETIC_RELAYS)
//                        .add(itemProvider);
            PonderRegistry.endRegistration();
            if(PonderJS.Settings.instance.autoGenerateLang) {
                JsonObject json = new JsonObject();
                PonderLocalization.generateSceneLang();
                PonderLocalization.record("kubejs", json);
                Triple<Boolean, ITextComponent, Integer> result = PonderJS.generateJsonLang(new Gson().fromJson(json, HashMap.class));
                boolean success = result.a;
                int count = result.c;
                if(success) {
                    if(count > 0) {
                        KubeJS.PROXY.reloadLang();
                        Minecraft.getInstance().reloadResourcePacks();
                    }
                } else {
                    PonderJS.generatePonderLang();
                }
            } else {
                PonderJS.generatePonderLang();
            }
        });
    }

}
