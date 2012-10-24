 Hudson Heckler
================

Hudson Heckler is a simple program that'll keep you updated on what's happening within a team's Hudson CI build manager on Linux, Windows and Mac OS X over a choice of two backends: *libnotify (via java-gnome)* and *growl*. You won't have to worry about this thing being too much of an annoyance as it'll only pipe up when there's something _really important_ to say, such as:

> ![Something is broken since this build!](http://i.imgur.com/pt3HW.png "This happens when someone breaks something...")

> ![Everything is back to normal!](http://i.imgur.com/bLnZF.png "...and this one happens when it's all fixed up...")

> ![The build was aborted!](http://i.imgur.com/5L84x.png "...you may see this if someone doesn't want to know they've destroyed your build, but now you'll be able to catch them!")

Installing it
-------------

Getting this up and running isn't all too difficult. Extract everything within
this zip file into a directory of your choice and edit notifier.config as you so desire.
The jar is executable by the Java runtime binaries; if you have a bad OS that doesn't
acknowledge this automatically, you could run this command in a terminal instead:

    $ java -jar HudsonHeckler-*.jar

Please note that gtk.jar must exist in /usr/share/java if you plan to use the
gnome notifier. On Ubuntu, you may get this through apt using this command in a terminal:

    $ sudo apt-get install libjava-gnome-java

Configuring it
--------------

There's a configuration file included alongside the program that'll allow you to change
some of its runtime settings. Simply right click on the program's icon and hit "Edit
Configuration"; just be sure to save the file and restart the program when you're done.

Compiling it
------------

The included Ant build script should take care of everything for you. There's something
a little special you'll have to do if you don't intend to install the java-gnome library
on your build platform that'll quell any compilation errors you may get regarding that.

### With support for both Growl and Gnome ###

    $ ant

### With Growl-only support ###

    $ ant -Dgnomeless=true

### Running the tests ###

    $ ant test

## Will it work with Jenkins or my other mutant derivative? ###

Probably. We use version 2.2.0 of the Hudson flavour at work but as long as there's an
```/rssLatest``` feed that bares a significant enough likeness, you'll be fine.
This also applies to other variants and strangely modified versions of the software.

## Contributions are welcome! ###

Please feel free to contribute to this project; there's plenty of potential here and
if you've free time to spare you should totally spend it on this project.
Really. You know it makes sense.
