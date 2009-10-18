Description
-----------

GShell - A command-line shell framework.

Building
--------

### Requirements

* Maven 2.x
* Java 5
* JLine 2.x
* Gossip 1.x

Check out and build:

    git clone git://github.com/jdillon/gshell.git
    cd gshell
    mvn install

After this completes, you can unzip the assembly and launch the shell:

    gunzip -c gshell-assembly/target/gshell-*-bin.tar.gz | tar xf -
    ./gshell-*/bin/gsh
