package pl.anarchyfakekeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

public final class Helpers {
    /** Tekst z kodami § bez domyślnej kursywy (nazwy/lore). */
    public static Text t(String s) {
        return Text.literal(s).styled(st -> st.withItalic(false));
    }

    public static void play(SoundEvent e, float pitch) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(e, pitch));
    }

    public static void play(RegistryEntry<SoundEvent> e, float pitch) {
        play(e.value(), pitch);
    }

    /** Wkłada przedmiot do wolnego slotu. Creative: pakiet; reszta: tylko wizualnie po stronie klienta. */
    public static boolean give(ItemStack stack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null) return false;
        PlayerInventory inv = p.getInventory();
        for (int i = 0; i < 36; i++) {
            if (inv.getStack(i).isEmpty()) {
                inv.setStack(i, stack.copy());
                if (mc.interactionManager != null && mc.interactionManager.getCurrentGameMode().isCreative()) {
                    mc.interactionManager.clickCreativeStack(stack.copy(), i < 9 ? 36 + i : i);
                }
                return true;
            }
        }
        p.sendMessage(Text.literal("§cBrak wolnego slotu w ekwipunku!"), true);
        return false;
    }

    private Helpers() {}
}
