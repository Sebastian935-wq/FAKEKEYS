package pl.anarchyfakekeys;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AnarchyFakeKeysClient implements ClientModInitializer {
    public static final String MOD_ID = "anarchyfakekeys";
    private static KeyBinding openKey;
    private static boolean pendingOpen;

    @Override
    public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.anarchyfakekeys.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.anarchyfakekeys"));

        // Komenda: ustawiamy flagę, bo ChatScreen po wysłaniu komendy sam robi setScreen(null)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("fakeklucze").executes(ctx -> {
                pendingOpen = true;
                return 1;
            })));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.wasPressed()) pendingOpen = true;
            if (pendingOpen && mc.player != null) {
                pendingOpen = false;
                mc.setScreen(new FakeKeysScreen());
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!world.isClient) return ActionResult.PASS;
            Block b = world.getBlockState(hit.getBlockPos()).getBlock();
            if (b != Blocks.CHEST && b != Blocks.TRAPPED_CHEST && b != Blocks.ENDER_CHEST) return ActionResult.PASS;

            KeyType key = KeyType.fromStack(player.getMainHandStack());
            if (key == null) key = KeyType.fromStack(player.getOffHandStack());
            if (key == null && !Config.allFake) return ActionResult.PASS;
            if (key == null) {
                key = b == Blocks.CHEST ? KeyType.ANARCHICZNY
                    : b == Blocks.TRAPPED_CHEST ? KeyType.EPICKI : KeyType.RZADKI;
            }

            if (hand == Hand.MAIN_HAND) {
                KeyType k = key;
                MinecraftClient mc = MinecraftClient.getInstance();
                mc.execute(() -> mc.setScreen(new FakeCrateScreen(k)));
            }
            // FAIL zamiast SUCCESS: Fabric przy zaakceptowanym wyniku i tak wysyła pakiet do serwera,
            // a FAIL anuluje interakcję po stronie klienta bez żadnego pakietu.
            return ActionResult.FAIL;
        });
    }
}
