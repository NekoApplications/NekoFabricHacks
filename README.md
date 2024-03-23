<div align="center">
   <img width="160" src="src/main/resources/assets/neko_fabric_hacks/icon_2.png" alt="logo"/>

[![AWESOME KOTLIN](https://img.shields.io/badge/awesome-kotlin-purple)](https://kotlinlang.org/)


----

Neko Fabric Hacks provides a series of (not really) useful functionality
    <h4>Made by 🐱<h4/>

</div>


## Neko Fabric Hacks

### Functionality

- (A little more) verbose log messages:
  - Logs out mod entrypoints loading information  
     ```
     Loading Main Entrypoint(me.jellysquid.mods.lithium.common.LithiumMod) of Mod lithium 0.12.1
     ```
  - Logs out Mixin config selecting and mixin config plugin loading information.
     ```
     Selecting Mixin Config mixinextras.init.mixins.json
     Loading Mixin Config Plugin com.llamalad7.mixinextras.platform.fabric.MixinExtrasConfigPlugin
     ```
  - Logs out Mixin Apply info
     ```
     Applying Mixin [icu.takeneko.nfh.mixin.CommandManagerMixin(from neko_fabric_hacks.mixins.json) -> net/minecraft/server/command/CommandManager] at stage MAIN
     Applying Mixin [icu.takeneko.nfh.mixin.CommandManagerMixin(from neko_fabric_hacks.mixins.json) -> net/minecraft/server/command/CommandManager] at stage PREINJECT
     Applying Mixin [icu.takeneko.nfh.mixin.CommandManagerMixin(from neko_fabric_hacks.mixins.json) -> net/minecraft/server/command/CommandManager] at stage INJECT
    ```
