# OfflineLAN — Forge module (1.19.4 – 1.20.6)

Forge port of OfflineLAN. Lets non-premium (offline / cracked) accounts join
your LAN world. Only the host needs the mod.

Local build (defaults to MC 1.20.1, Forge 47.4.10):

```bash
./gradlew build
# jar -> build/libs/offlinelan-1.1.0.jar (reobfuscated)
```

Other MC versions are built by the CI matrix at the repo root
(`.github/workflows/`), which passes `-Pminecraft_version=...`,
`-Pforge_version=...`, `-Pminecraft_version_range=...`,
`-Pforge_version_range=...`, `-Ploader_version_range=...`,
`-Pmapping_version=...` and `-Ppack_format=...`.
