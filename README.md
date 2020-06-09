# Compass Tracker
A hunter vs. speedrunner plugin for servers.

For Minecraft 1.15 and above.

# Features
- Track a certain player's location via a special compass. Right click the special compass to get their updated location.
- Commands to start and stop the game.
- Ability to set who the compass will track. Only supports 1 person tracking.
- Support for multiple hunters who can use the compass.
- Very lightweight, super easy to use.

# How to Use
1. Make sure there are at least 2 players on the server.
2. Use /ct setplayer <player> to set the tracked player. This is who the compass will point to.
3. Use /ct addhunter <player> to add a hunter to the game. You can have multiple people that will use the tracking compass.
    - Use /ct removehunter to remove a hunter.
    - Use /ct listhunters to see the current hunters.
4. Use /ct start to start the game. Hunters will receive the tracking compass. You must have the player and at least 1 hunter set.
5. Right click the compass to get their location. You will have to manually click it to update the compass. The compass will not point to the live location.
6. Use /ct stop to stop the game.
