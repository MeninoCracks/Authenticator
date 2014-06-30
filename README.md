Authenticator
=============

A Bukkit plugin for offline mode Minecraft which forces players to register/login in order to play the game.

INSTALL
=============

Put the jar file into your server's plugin directory, and everything is good to go.  It will create a plaintext database in
the same directory that CraftBukkit is running in.  Passwords are hashed with BCrypt and cannot be restored if lost.

This project was made for Bukkit 1.7.9, as no similar plugins were available to be used.

FEATURES
=============

/login password 


/register password     


When a player joins the server, they will not be able to move, break blocks, drop items, take damage, chat, use commands, etc until they login.  Each username can only be registered once.  This protects offline servers from
malicious players who will try to login to accounts that they do not own.
