# OfflineLAN — Fabric module

Fabric port of OfflineLAN. Lets non-premium (offline / cracked) accounts join
your LAN world. Only the host needs the mod (+ Fabric API).

Local build (defaults to MC 1.21.1):

```bash
./gradlew build
# jar -> build/libs/offlinelan-1.1.0.jar
```

Other MC versions are built by the CI matrix at the repo root
(`.github/workflows/`), which passes `-Pminecraft_version=...`,
`-Ploader_version=...`, `-Ploom_version=...`, `-Pfabric_api_version=...`,
`-Pjava_release=...` and the `fabric.mod.json` dependency ranges.
