package pl.anarchyfakekeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeCrateScreen extends Screen {
    private static final Identifier TEX = Identifier.of("minecraft", "textures/gui/container/generic_54.png");
    private static final int SPIN_TICKS = 60; // 3 sekundy

    private final KeyType type;
    private final KeyType.Prize winner;
    private final List<ItemStack> seq = new ArrayList<>();
    private final int totalSteps;
    private int ticks, step, after;
    private boolean done;

    private static ItemStack filler(Item item) {
        ItemStack s = new ItemStack(item);
        s.set(DataComponentTypes.CUSTOM_NAME, Text.literal(" "));
        return s;
    }

    private static int interval(int t) { return t < 30 ? 2 : t < 48 ? 3 : 5; }

    public FakeCrateScreen(KeyType type) {
        super(Text.literal(type.crate));
        this.type = type;
        List<KeyType.Prize> pool = type.prizes();
        Random r = new Random();
        this.winner = pool.get(r.nextInt(pool.size()));

        int s = 0;
        for (int t = 1; t < SPIN_TICKS; t++) if (t % interval(t) == 0) s++;
        this.totalSteps = s;
        for (int i = 0; i < s + 9; i++) seq.add(pool.get(r.nextInt(pool.size())).stack().copy());
        seq.set(s + 4, winner.stack().copy()); // środek okna w ostatnim kroku = zwycięzca
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void tick() {
        if (done) { if (++after > 50) close(); return; }
        ticks++;
        if (ticks >= SPIN_TICKS) {
            finish();
        } else if (ticks % interval(ticks) == 0) {
            step++;
            Helpers.play(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 0.7f + 0.8f * step / Math.max(1, totalSteps));
        }
    }

    private void finish() {
        done = true;
        step = totalSteps;
        MinecraftClient mc = MinecraftClient.getInstance();
        Helpers.give(winner.stack().copy());
        Helpers.play(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f);
        if (mc.player != null) {
            String nick = mc.player.getName().getString();
            mc.player.sendMessage(Text.literal("§8[§c§lANARCHIA§8] §7Gracz §f" + nick
                + " §7wylosował " + winner.label() + " §7ze skrzyni!"), false);
        }
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        super.render(c, mx, my, delta);
        int x = (width - 176) / 2, y = (height - 168) / 2;
        c.drawTexture(RenderLayer::getGuiTextured, TEX, x, y, 0, 0, 176, 71, 256, 256);
        c.drawTexture(RenderLayer::getGuiTextured, TEX, x, y + 71, 0, 126, 176, 96, 256, 256);
        c.drawText(textRenderer, Text.literal(type.crate), x + 8, y + 6, 0x404040, false);
        c.drawText(textRenderer, Text.literal("Ekwipunek"), x + 8, y + 74, 0x404040, false);

        for (int i = 0; i < 27; i++) {
            int row = i / 9, col = i % 9;
            ItemStack s = row == 1 ? seq.get(step + col) : (col == 4 ? PTR() : FILL());
            int sx = x + 8 + col * 18, sy = y + 18 + row * 18;
            if (row == 1 && col == 4) c.fill(sx, sy, sx + 16, sy + 16, done ? 0x8055FF55 : 0x80FFFF55);
            c.drawItem(s, sx, sy);
            c.drawStackOverlay(textRenderer, s, sx, sy);
        }

        // ekwipunek gracza (tylko podgląd)
        if (client != null && client.player != null) {
            PlayerInventory inv = client.player.getInventory();
            for (int r = 0; r < 3; r++) for (int col = 0; col < 9; col++) {
                ItemStack s = inv.getStack(9 + r * 9 + col);
                c.drawItem(s, x + 8 + col * 18, y + 85 + r * 18);
                c.drawStackOverlay(textRenderer, s, x + 8 + col * 18, y + 85 + r * 18);
            }
            for (int col = 0; col < 9; col++) {
                ItemStack s = inv.getStack(col);
                c.drawItem(s, x + 8 + col * 18, y + 143);
                c.drawStackOverlay(textRenderer, s, x + 8 + col * 18, y + 143);
            }
        }
    }

    private static ItemStack FILL_S, PTR_S;
    private static ItemStack FILL() { return FILL_S != null ? FILL_S : (FILL_S = filler(Items.BLACK_STAINED_GLASS_PANE)); }
    private static ItemStack PTR()  { return PTR_S  != null ? PTR_S  : (PTR_S  = filler(Items.LIME_STAINED_GLASS_PANE)); }
}
