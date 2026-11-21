import json, os
from PIL import Image

MOD = "anarchyfakekeys"
ROOT = os.path.join("src", "main", "resources", "assets", MOD)
KEYS = {
    "anarchiczny_klucz": (200, 30, 30),    # czerwony
    "epicki_klucz":      (140, 40, 200),   # fioletowy
    "rzadki_klucz":      (60, 190, 230),   # błękitny
    "pety_klucz":        (240, 110, 200),  # różowy
}
SHAPE = [
    "................",
    "......oooo......",
    ".....obhhbo.....",
    "....obh..hbo....",
    "....obh..hbo....",
    ".....obhhbo.....",
    "......obbo......",
    "......obbo......",
    "......obbo......",
    "......obbooo....",
    "......obbbbo....",
    "......obbooo....",
    "......obbo......",
    "......obbbbo....",
    "......obbbbo....",
    "......oooooo....",
]

def w(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

for name, base in KEYS.items():
    pal = {
        "o": tuple(int(c * 0.4) for c in base) + (255,),
        "b": base + (255,),
        "h": tuple(min(255, c + 80) for c in base) + (255,),
        ".": (0, 0, 0, 0),
    }
    img = Image.new("RGBA", (16, 16))
    for y, row in enumerate(SHAPE):
        for x, ch in enumerate(row):
            img.putpixel((x, y), pal[ch])
    tex = os.path.join(ROOT, "textures", "item", f"{name}.png")
    os.makedirs(os.path.dirname(tex), exist_ok=True)
    img.save(tex)

    w(os.path.join(ROOT, "models", "item", f"{name}.json"), {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"{MOD}:item/{name}"},
    })
    w(os.path.join(ROOT, "items", f"{name}.json"), {
        "model": {"type": "minecraft:model", "model": f"{MOD}:item/{name}"},
    })

w(os.path.join(ROOT, "lang", "en_us.json"), {
    "key.anarchyfakekeys.open": "Otwórz generator kluczy",
    "category.anarchyfakekeys": "AnarchyFakeKeys",
})
print("Gotowe.")
