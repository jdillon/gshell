<!--

    Copyright (c) 2009-2013 the original author or authors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
Description
-----------

GShell - A command-line shell framework.

License
-------

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)


Building
--------

### Requirements

* Maven 3.0.x (3.1+ not supported)
* Java 5+

Check out and build:

    git clone git://github.com/sonatype/gshell.git
    cd gshell
    mvn install

After this completes, you can unzip the assembly and launch the shell:

    unzip gshell-dist/gshell-assembly/target/gshell-*-bin.zip
    ./gshell-*/bin/gsh

Use the 'help' command for further assistance.

Implementations
---------------

GShell is used by the [Maven Shell](https://github.com/jdillon/mvnsh)