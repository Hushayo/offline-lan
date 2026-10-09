# OfflineLAN — NeoForge module

NeoForge port of OfflineLAN (MC 1.20.4 – 1.21.11, 26.1 – 26.3).
Lets non-premium (offline / cracked) accounts join your LAN world.
Only the host needs the mod.

Local build (defaults to MC 1.21.1, NeoForge 21.1.257):

```bash
./gradlew build
# jar -> build/libs/offlinelan-1.1.0.jar
```

Other MC versions are built by the CI matrix at the repo root
(`.github/workflows/`), which passes `-Pminecraft_version=...`,
`-Pneo_version=...`, `-Pparchment_*=...`, `-Ptoml_variant=legacy|modern`
and `-Pjava_version=...`.
