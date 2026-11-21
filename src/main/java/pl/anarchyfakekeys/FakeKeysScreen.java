package pl.anarchyfakekeys;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class FakeKeysScreen extends Screen {
    private final KeyType[] types = KeyType.values();
    private final ButtonWidget[] keyBtns = new ButtonWidget[4];
    private final ItemStack[] icons = new ItemStack[4];
    private ButtonWidget qtyBtn, allBtn;

    public FakeKeysScreen() {
        super(Text.literal("Anarchia.GG - Generator Kluczy"));
    }

    private Text label(KeyType k) {
        return Text.literal("[ +" + Config.qty() + "x " + k.button + " ]");
    }

    private void refresh() {
        for (int i = 0; i < 4; i++) keyBtns[i].setMessage(label(types[i]));
        qtyBtn.setMessage(Text.literal("Ilość: " + Config.qty() + "x"));
        allBtn.setMessage(Text.literal("Wszystkie skrzynki fejkowe: " + (Config.allFake ? "§aTAK" : "§cNIE")));
    }

    @Override
    protected void init() {
        for (int i = 0; i < 4; i++) {
            KeyType k = types[i];
            icons[i] = k.stack(1);
            int x = width / 2 + (i % 2 == 0 ? -155 : 5);
            int y = 50 + (i / 2) * 30;
            keyBtns[i] = addDrawableChild(ButtonWidget.builder(label(k), b -> {
                if (Helpers.give(k.stack(Config.qty()))) {
                    // dźwięk kliknięcia odtwarza już sam vanilla przycisk
                    Helpers.play(SoundEvents.ENTITY_ITEM_PICKUP, 1.0f);
                }
            }).dimensions(x, y, 150, 20).build());
        }
        qtyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            Config.qtyIdx = (Config.qtyIdx + 1) % Config.QTY.length;
            refresh();
        }).dimensions(width / 2 - 75, 110, 150, 20).build());

        allBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            Config.allFake = !Config.allFake;
            refresh();
        }).dimensions(width / 2 - 100, 140, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, b -> close())
            .dimensions(width / 2 - 100, height - 28, 200, 20).build());
        refresh();
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        super.render(c, mx, my, delta); // renderuje tło (nie wołaj renderBackground drugi raz!)
        c.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
        for (int i = 0; i < 4; i++) {
            int x = width / 2 + (i % 2 == 0 ? -155 : 5);
            int y = 50 + (i / 2) * 30;
            c.drawItem(icons[i], i % 2 == 0 ? x - 22 : x + 156, y + 2);
        }
    }
}
