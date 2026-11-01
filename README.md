# Create: Luviteium

Create: Luviteium is a Minecraft 1.21.1 NeoForge addon built around a rare,
green-tinted material and Create automation.

## Development baseline

- Java 21
- Minecraft 1.21.1
- NeoForge 21.1.229
- Create 6.0.10 (development artifact `6.0.10-280`)

See [`docs/implementation-status.md`](docs/implementation-status.md) for the
current milestone status and known compatibility gaps. The original design
specification remains in [`create-luviteium-implementation-spec.md`](create-luviteium-implementation-spec.md).

## Texture status

The current 16x16 item and machine textures are placeholder-quality originals
in Create's visual language; worn armor already uses the dedicated green
`luviteium_layer_1`/`luviteium_layer_2` files. Textures exist to validate
silhouettes, transparency, face orientation, and machine readability, and are
expected to be replaced with final art later without changing the registry
names or model-facing texture paths.

## Build

Once Java 21 is installed, run:

```sh
./gradlew build
```

The first Gradle sync downloads and decompiles Minecraft/NeoForge dependencies.
The current implementation includes the casting table with hand-based mold
insertion and output extraction, a Molten Luviteium Bucket for pouring or
drawing molten charges by hand, Create fluid processing, Deployer mold flow,
the cooled-ingot product loop (fan splashing, panel pressing, block packing),
tool and armor sets with real vanilla tool classes and complete tool icons,
the 18-point armor set, the Biomass Generator, and a Luviteium creative tab.
See
[`docs/implementation-status.md`](docs/implementation-status.md) and
[`docs/qa-report.md`](docs/qa-report.md) for verified behavior and remaining manual
gameplay checks.
