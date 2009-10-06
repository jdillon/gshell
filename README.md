Description
-----------

GShell - A command-line shell framework.

Building
--------

### Requirements

* Maven 2.x
* Java 5
* JLine 2.0

At the moment JLine 2.0 must be built manually, as it is not yet in any public repositories:

    git clone git://github.com/jdillon/jline2.git
    cd jline2
    mvn install

Check out and build:

    git clone git://github.com/jdillon/gshell.git
    cd gshell
    mvn install

After this completes, you can unzip the assembly and launch the shell:

    gunzip -c gshell-assembly/target/gshell-*-bin.tar.gz | tar xf -
    ./gshell-*/bin/gsh
