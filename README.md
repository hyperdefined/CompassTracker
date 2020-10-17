# CompassTracker
![Build with Maven](https://github.com/hyperdefined/CompassTracker/workflows/Java%20CI%20with%20Maven/badge.svg) [![Downloads](https://img.shields.io/github/downloads/hyperdefined/CompassTracker/total?logo=github)](https://github.com/hyperdefined/CompassTracker/releases) [![Donate with Bitcoin](https://en.cryptobadges.io/badge/micro/1F29aNKQzci3ga5LDcHHawYzFPXvELTFoL)](https://en.cryptobadges.io/donate/1F29aNKQzci3ga5LDcHHawYzFPXvELTFoL) [![Donate with Ethereum](https://en.cryptobadges.io/badge/micro/0x0f58B66993a315dbCc102b4276298B5Ff8895F41)](https://en.cryptobadges.io/donate/0x0f58B66993a315dbCc102b4276298B5Ff8895F41)

A hunter vs. speedrunner plugin for servers.

For Minecraft 1.15 and above.

# Features
- Track a player's location via a compass. Right click the compass, and it will point to their location.
- Support for multiple hunters who can use the compass.
- Very lightweight, super easy to use.
- The game will end once the player kills the Ender Dragon.

# How to Use
1. Make sure there are at least 2 players on the server.
2. Use `/ct setplayer <player>` to set the tracked player. This is who the compass will point to.
3. Use `/ct addhunter <player>` to add a hunter to the game. You can have multiple people that will use the tracking compass.
    - Use `/ct removehunter` to remove a hunter.
    - Use `/ct listhunters` to see the current hunters.
4. Use /ct start to start the game. Hunters will receive the tracking compass. You must have the player and at least 1 hunter set.
5. Right click the compass to get their location. You will have to manually click it to update the compass. The compass will not point to the live location.
6. Use /ct stop to stop the game.

# Permissions
- `compasstracker.start`
    - /ct start
- `compasstracker.stop`
    - /ct stop
- `compasstracker.setplayer`
    - /ct setplayer
- `compasstracker.removeplayer`
    - /ct removeplayer
- `compasstracker.addhunter`
    - /ct addhunter
- `compasstracker.removehunter`
    - /ct removehunter
