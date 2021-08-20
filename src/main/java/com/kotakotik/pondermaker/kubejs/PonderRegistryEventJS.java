package com.kotakotik.pondermaker.kubejs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.antlr.v4.runtime.misc.Triple;

import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PonderRegistryEventJS extends EventJS {

    public PonderBuilderJS create(String name, Object items) {
        return new PonderBuilderJS(name, ListJS.orSelf(items));
    }

    public void register(FMLClientSetupEvent event) {
//                PonderRegistry.forComponents(itemProvider)
//                        .addStoryBoard("test", b.function::accept);
//                PonderRegistry.TAGS.forTag(PonderTag.KINETIC_RELAYS)
//                        .add(itemProvider);
        event.enqueueWork(() -> {
            try {
                PonderJS mainJS = PonderJS.get();
                ScriptType scriptType = ScriptType.STARTUP;
                mainJS.tagRegistryEvent.post(scriptType, "ponder.tag.registry");
                mainJS.tagItemEvent.post(scriptType, "ponder.tag");
                mainJS.ponderEvent.post(scriptType, "ponder.registry");

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
            } catch (Exception e) { // i think theres a way to do this with the completable future but this is easier
                e.printStackTrace();
            }
        });
    }

}
