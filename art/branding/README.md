# Create: Luviteium logo

An original green ingot and dark mechanical gear with aged-brass accents. The
symbol is used in the repository README and NeoForge's mod list. In-game textures,
including armor, remain placeholders; this branding pass does not change them.

- **Generated source:** [logo-source.png](logo-source.png).
- **Exact prompt:** [logo-prompt.txt](logo-prompt.txt).
- **Runtime icon:** [logo.png](../../src/main/resources/logo.png), RGBA 256×256.
- **Method:** built-in `imagegen`, transparent-background generation; no CLI/API
  fallback. Original output identifier: `exec-a19f696d-0a4d-4056-815f-ea9a1c383c25.png`.

The full-size original is retained here. The runtime icon is resized and centered
without changing the generated design or removing its alpha channel:

```sh
magick art/branding/logo-source.png -resize 256x256 -gravity center -background none -extent 256x256 src/main/resources/logo.png
```

The icon was inspected at full size and 48×48 for small-size readability. No
official Minecraft or Create logo was used as an input or copied.
