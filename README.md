Description
-----------

GShell - A command-line shell framework.

License
-------

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Support
-------

To submit an issue, please use the [Sonatype Issue Tracker](https://issues.sonatype.org/browse/MVNSH).

Building
--------

### Requirements

* Maven 3+ (3.0-beta-3+)
* Java 5+

Check out and build:

    git clone git://github.com/sonatype/gshell.git
    cd gshell
    mvn install

After this completes, you can unzip the assembly and launch the shell:

    unzip gshell-assembly/target/gshell-*-bin.zip
    ./gshell-*/bin/gsh
