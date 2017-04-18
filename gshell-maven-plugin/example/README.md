<!--

    Copyright (c) 2009-present the original author or authors.

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
# Example

Shows how to use Apache Maven as an application-launcher to configure the execution of GShell to include specific environment and plugins.

## Running
 
    ./example

Output:

    [INFO] Scanning for projects...
    [INFO] 
    [INFO] ------------------------------------------------------------------------
    [INFO] Building example 1
    [INFO] ------------------------------------------------------------------------
    [INFO] 
    [INFO] --- gshell-maven-plugin:3.0.0-SNAPSHOT:run (default-cli) @ example ---
    
    Type 'help' for more information.
    -------------------------------------------------------------------------------
    example>
    
Run a command; 'set' to list all defined variables:

    example> set
    shell.logging='INFO'
    shell.errors='false'
    shell.home='/Users/jason/ws/planet57/gshell/gshell-maven-plugin/example/.gshell'
    shell.version='1'
    shell.user.home='/Users/jason'
    shell.prompt='@|bold example-shell|@> '
    shell.user.dir='/Users/jason/ws/planet57/gshell/gshell-maven-plugin/example'
    shell.group='/'
    shell.group.path='.:/'
    example>

## State

GShell for Apache Maven will store state under:

    $HOME/.m2/gshell/<program>
