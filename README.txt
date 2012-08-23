 Hudson Notifier v0.1
----------------------

Hudson Notifier is a simple program that'll keep you updated on what's happening 
within your Hudson CBI manager. It currently supports showing notifications for 
various build events over two backends: gnome (gtk) and growl.

- INSTALLATION -

Getting this up and running isn't all too difficult. Extract everything within 
this zip file into a directory of your choice and edit notifier.config as you may.
The jar is executable by the Java runtime binaries; if your OS is crap and doesn't 
acknowledge this automatically, you could run this command in a terminal instead:

   java -jar HudsonNotifier-*.jar

Please note that gtk.jar must exist in /usr/share/java. On Ubuntu, you may get this
through apt:

   sudo apt-get install libjava-gnome-java

- Luke
