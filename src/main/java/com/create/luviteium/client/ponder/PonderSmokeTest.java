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
    private static boolean capturePending;
    private static final String[] ITEMS = {"casting_table", "blank_mold", "biomass_generator"};
    private static final String[] SHOTS = {"casting_table", "blank_mold", "biomass_generator",
            "biomass_generator-turning", "biomass_generator-stopped", "biomass_generator-stopped-later",
            "processing-mixing", "processing-hot-cast", "processing-cooling"};
    private static final int[] SHOT_TIMES = {150, 270, 150, 157, 360, 365, 60, 238, 340};
    private static float previousShaftAngle;

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
                        case "casting_table" -> new net.minecraft.core.BlockPos(4, 2, 3);
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
        PonderUI ui = PonderUI.of(ResourceLocation.fromNamespaceAndPath("luviteium",
                ITEMS[screenshotIndex >= 6 ? 0 : Math.min(screenshotIndex, 2)]));
        mc.setScreen(ui);
        ui.seekToTime(SHOT_TIMES[screenshotIndex]);
        screenshotTicks = 0;
    }

    private static void capture(Minecraft mc) {
        if (screenshotIndex >= SHOTS.length || capturePending) return;
        if (++screenshotTicks < (screenshotIndex == 7 ? 8 : 25)) return;
        if (screenshotIndex == 0 || screenshotIndex == 7) {
            var scene = ((PonderUI) mc.screen).getActiveScene();
            var table = (com.create.luviteium.content.CastingTableBlockEntity)
                    scene.getWorld().getBlockEntity(new net.minecraft.core.BlockPos(4, 2, 3));
            if (!(mc.getBlockEntityRenderDispatcher().getRenderer(table)
                    instanceof com.create.luviteium.client.CastingTableRenderer))
                throw new IllegalStateException("Missing casting animation renderer");
            if (screenshotIndex == 0 && table.getVisualFill(1) <= 0)
                throw new IllegalStateException("Filled mold has no visible liquid");
            // Seeking replays instructions over multiple UI ticks. Capture the actual
            // completion pulse rather than assuming the seek finished on a fixed frame.
            if (screenshotIndex == 7 && (table.getDisplayedOutput().isEmpty() || table.getEjectionLift(0) < 0.12f)) {
                if (screenshotTicks < 100) return;
                throw new IllegalStateException("Cast did not show a completed product with an ejection animation");
            }
            LogUtils.getLogger().info("LUVITEIUM_CASTING_RENDER_PASS {} fill={} lift={}",
                    SHOTS[screenshotIndex], table.getVisualFill(1), table.getEjectionLift(0));
        }
        if (screenshotIndex >= 2 && screenshotIndex <= 5) {
            var ui = (PonderUI) mc.screen;
            var generator = (com.create.luviteium.content.BiomassGeneratorBlockEntity)
                    ui.getActiveScene().getWorld().getBlockEntity(new net.minecraft.core.BlockPos(3, 1, 3));
            if (!(mc.getBlockEntityRenderDispatcher().getRenderer(generator)
                    instanceof com.create.luviteium.client.BiomassGeneratorRenderer))
                throw new IllegalStateException("Missing generator shaft renderer");
            float expectedSpeed = screenshotIndex >= 4 ? 0 : 64;
            if (generator.getSpeed() != expectedSpeed)
                throw new IllegalStateException("Expected shaft speed " + expectedSpeed + ", got " + generator.getSpeed());
            float angle = com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.getAngleForBe(
                    generator, generator.getBlockPos(), net.minecraft.core.Direction.Axis.X);
            if (screenshotIndex == 3 && angle == previousShaftAngle)
                throw new IllegalStateException("Powered shaft angle did not advance");
            if (screenshotIndex == 5 && angle != previousShaftAngle)
                throw new IllegalStateException("Stopped shaft angle changed");
            previousShaftAngle = angle;
            LogUtils.getLogger().info("LUVITEIUM_SHAFT_RENDER_PASS {} speed={} angle={}", SHOTS[screenshotIndex], expectedSpeed, angle);
        }
        capturePending = true;
    }

    @SubscribeEvent
    public static void captureRenderedFrame(net.neoforged.neoforge.client.event.RenderFrameEvent.Post event) {
        if (!capturePending) return;
        capturePending = false;
        Minecraft mc = Minecraft.getInstance();
        // Client ticks precede rendering; reading the target there captures an older frame.
        try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
            Files.createDirectories(Path.of("screenshots"));
            image.writeToFile(Path.of("screenshots", "ponder-" + SHOTS[screenshotIndex] + ".png"));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not save Ponder evidence", e);
        }
        if (++screenshotIndex < SHOTS.length) {
            showScene(mc);
        } else {
            LogUtils.getLogger().info("LUVITEIUM_PONDER_ALL_PASS (including UI rendering)");
            mc.stop();
        }
    }
}
