# fckU client
Red-themed Fabric client mod (MC 1.21.4): ESP, StashFinder, AutoCrystal, AutoAnchor.

Build: install JDK 21 + Gradle, run `gradle wrapper` once, then `./gradlew build`.
Jar lands in build/libs/fckU-client-1.0.0.jar -> drop in .minecraft/mods with Fabric Loader + Fabric API.

Controls: RIGHT SHIFT opens the ClickGUI, click a module to toggle it.
Modules: ESP (on by default), StashFinder, AutoCrystal, AutoAnchor.
Tweak range/delay/threshold values at the top of each module class.
