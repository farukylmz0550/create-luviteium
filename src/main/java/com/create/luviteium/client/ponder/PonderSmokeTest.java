package com.create.luviteium.client.ponder;

import com.create.luviteium.CreateLuviteium;
import com.mojang.logging.LogUtils;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.Difficulty;
import net.minecraft.client.Screenshot;
import java.nio.file.Files;
import java.nio.file.Path;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Opt-in client test: ./gradlew runClient -PponderSmokeTest. */
@EventBusSubscriber(modid = CreateLuviteium.MOD_ID, value = Dist.CLIENT)
public final class PonderSmokeTest {
    private static boolean ran;
    private static boolean opening;
    private static int screenshotIndex;
    private static int screenshotTicks;
    private static final String[] ITEMS = {"casting_table", "blank_mold", "biomass_generator"};

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("luviteium.ponderSmokeTest")) return;
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isGameLoadFinished() || mc.getOverlay() != null) return;
        if (ran) {
            capture(mc);
            return;
        }
        if (mc.level == null) {
            if (!opening) {
                opening = true;
                mc.createWorldOpenFlows().createFreshLevel("luviteium-ponder-smoke-" + System.currentTimeMillis(),
                        new LevelSettings("Luviteium Ponder QA", GameType.CREATIVE, false,
                                Difficulty.PEACEFUL, true, new GameRules(), WorldDataConfiguration.DEFAULT),
                        new WorldOptions(1, false, false),
                        registries -> registries.registryOrThrow(Registries.WORLD_PRESET)
                                .getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions(), mc.screen);
            }
            return;
        }
        if (mc.player == null) return;
        ran = true;
        try {
            for (String item : ITEMS) {
                var scenes = PonderIndex.getSceneAccess().compile(ResourceLocation.fromNamespaceAndPath("luviteium", item));
                if (scenes.size() != 1) throw new IllegalStateException("Expected one scene for " + item);
                var scene = scenes.getFirst();
                if (scene.getWorld().getBlockState(new net.minecraft.core.BlockPos(0, 0, 0)).isAir())
                    throw new IllegalStateException("Missing workshop template");
                for (int replay = 0; replay < 2; replay++) {
                    scene.begin();
                    for (int i = 0; i < 650; i++) scene.tick();
                    if (!scene.isFinished()) throw new IllegalStateException("Scene did not finish: " + item);
                    var machine = switch (item) {
                        case "casting_table" -> new net.minecraft.core.BlockPos(4, 1, 3);
                        case "blank_mold" -> new net.minecraft.core.BlockPos(2, 3, 3);
                        default -> new net.minecraft.core.BlockPos(3, 1, 3);
                    };
                    if (scene.getWorld().getBlockState(machine).isAir())
                        throw new IllegalStateException("Missing scene machine: " + item);
                }
                LogUtils.getLogger().info("LUVITEIUM_PONDER_PASS {} (playback + replay)", item);
            }
            showScene(mc);
        } catch (Throwable failure) {
            LogUtils.getLogger().error("LUVITEIUM_PONDER_FAILED", failure);
            throw new IllegalStateException("Ponder smoke test failed", failure);
        }
    }

    private static void showScene(Minecraft mc) {
        PonderUI ui = PonderUI.of(ResourceLocation.fromNamespaceAndPath("luviteium", ITEMS[screenshotIndex]));
        mc.setScreen(ui);
        ui.seekToTime(screenshotIndex == 1 ? 270 : 150);
        screenshotTicks = 0;
    }

    private static void capture(Minecraft mc) {
        if (screenshotIndex >= ITEMS.length) return;
        if (++screenshotTicks < 25) return;
        try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
            Files.createDirectories(Path.of("screenshots"));
            image.writeToFile(Path.of("screenshots", "ponder-" + ITEMS[screenshotIndex] + ".png"));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not save Ponder evidence", e);
        }
        if (++screenshotIndex < ITEMS.length) {
            showScene(mc);
        } else {
            LogUtils.getLogger().info("LUVITEIUM_PONDER_ALL_PASS (including UI rendering)");
            mc.stop();
        }
    }
}
