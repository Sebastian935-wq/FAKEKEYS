package pl.anarchyfakekeys;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public enum KeyType {
    ANARCHICZNY("anarchiczny_klucz", "ANARCHICZNY KLUCZ", "§c§lANARCHICZNY KLUCZ", "§c§lANARCHICZNA SKRZYNIA",
        "§7Klucz ten otwiera §cAnarchiczną Skrzynię§7,",
        "§7w której znajdziesz najlepszy sprzęt na serwerze!",
        "",
        "§8» §7Użyj na spawnie przy skrzyni: §cAnarchiczna",
        "§8» §7Podgląd dropu: §fKliknij LPM na skrzynię"),
    EPICKI("epicki_klucz", "EPICKI KLUCZ", "§5§lEPICKI KLUCZ", "§5§lEPICKA SKRZYNIA",
        "§7Klucz ten otwiera §5Epicką Skrzynię§7,",
        "§7zawierającą rzadkie przedmioty i magiczne księgi.",
        "",
        "§8» §7Użyj na spawnie przy skrzyni: §5Epicka",
        "§8» §7Podgląd dropu: §fKliknij LPM na skrzynię"),
    RZADKI("rzadki_klucz", "RZADKI KLUCZ", "§b§lRZADKI KLUCZ", "§b§lRZADKA SKRZYNIA",
        "§7Klucz ten otwiera §bRzadką Skrzynię§7,",
        "§7z podstawowym wyposażeniem na start.",
        "",
        "§8» §7Użyj na spawnie przy skrzyni: §bRzadka",
        "§8» §7Podgląd dropu: §fKliknij LPM na skrzynię"),
    PETY("pety_klucz", "KLUCZ PETÓW", "§d§lKLUCZ DO SKRZYNI PETÓW", "§d§lSKRZYNIA PETÓW",
        "§7Klucz ten otwiera §dSkrzynię Petów§7,",
        "§7z której wylosujesz towarzysza dającego bonusy!",
        "",
        "§8» §7Użyj na spawnie przy skrzyni: §dSkrzynia Petów",
        "§8» §7Podgląd dropu: §fKliknij LPM na skrzynię");

    public record Prize(String label, ItemStack stack) {}

    public final String id, button, name, crate;
    private final String[] lore;

    KeyType(String id, String button, String name, String crate, String... lore) {
        this.id = id; this.button = button; this.name = name; this.crate = crate; this.lore = lore;
    }

    public ItemStack stack(int count) {
        ItemStack s = new ItemStack(Items.TRIPWIRE_HOOK, count);
        s.set(DataComponentTypes.CUSTOM_NAME, Helpers.t(name));
        s.set(DataComponentTypes.LORE, new LoreComponent(Arrays.stream(lore).map(Helpers::t).toList()));
        s.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        s.set(DataComponentTypes.ITEM_MODEL, Identifier.of(AnarchyFakeKeysClient.MOD_ID, id));
        return s;
    }

    public static KeyType fromStack(ItemStack s) {
        if (s == null || s.isEmpty()) return null;
        Identifier m = s.get(DataComponentTypes.ITEM_MODEL);
        if (m == null || !m.getNamespace().equals(AnarchyFakeKeysClient.MOD_ID)) return null;
        for (KeyType k : values()) if (k.id.equals(m.getPath())) return k;
        return null;
    }

    private static Prize p(Item item, int n, String label) {
        ItemStack s = new ItemStack(item, n);
        s.set(DataComponentTypes.CUSTOM_NAME, Helpers.t(label));
        s.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return new Prize(label, s);
    }

    public List<Prize> prizes() {
        return switch (this) {
            case ANARCHICZNY -> List.of(
                p(Items.NETHERITE_CHESTPLATE, 1, "§c§lSET PROT 5"),
                p(Items.NETHERITE_SWORD, 1, "§c§lMIECZ OSTROŚĆ 6"),
                p(Items.TOTEM_OF_UNDYING, 1, "§e§lTOTEM NIEŚMIERTELNOŚCI"),
                p(Items.ENCHANTED_GOLDEN_APPLE, 8, "§6§lKOXY x8"),
                p(Items.ELYTRA, 1, "§b§lELYTRA"),
                p(Items.NETHERITE_PICKAXE, 1, "§c§lKILOF EFEKTYWNOŚĆ 10"));
            case EPICKI -> List.of(
                p(Items.ENCHANTED_BOOK, 1, "§5§lKSIĘGA NAPRAWA"),
                p(Items.ENCHANTED_BOOK, 1, "§5§lKSIĘGA OSTROŚĆ 5"),
                p(Items.DIAMOND_CHESTPLATE, 1, "§5§lZBROJA PROT 4"),
                p(Items.ENCHANTED_GOLDEN_APPLE, 4, "§6§lKOXY x4"),
                p(Items.TOTEM_OF_UNDYING, 1, "§e§lTOTEM NIEŚMIERTELNOŚCI"));
            case RZADKI -> List.of(
                p(Items.DIAMOND_SWORD, 1, "§b§lMIECZ OSTROŚĆ 4"),
                p(Items.DIAMOND_PICKAXE, 1, "§b§lKILOF EFEKTYWNOŚĆ 5"),
                p(Items.GOLDEN_APPLE, 16, "§6§lJABŁKA x16"),
                p(Items.EXPERIENCE_BOTTLE, 32, "§a§lBUTELKI XP x32"),
                p(Items.DIAMOND, 16, "§b§lDIAMENTY x16"));
            case PETY -> List.of(
                p(Items.ALLAY_SPAWN_EGG, 1, "§d§lPET: ALLAY"),
                p(Items.AXOLOTL_SPAWN_EGG, 1, "§d§lPET: AXOLOTL"),
                p(Items.FOX_SPAWN_EGG, 1, "§d§lPET: LIS"),
                p(Items.WOLF_SPAWN_EGG, 1, "§d§lPET: WILK"),
                p(Items.PARROT_SPAWN_EGG, 1, "§d§lPET: PAPUGA"));
        };
    }
}
