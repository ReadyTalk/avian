Aviary
================================================



You will probably need some flavor of Java installed.  
(You might not for the OpenJDK version; you could try it, I guess.)  

If you don't already have a JRE (on 10.7 through 10.9):  
```
~> /System/Library/Frameworks/JavaVM.framework/Versions/A/Resources/MacOS/JavaApplicationStub
```  

...will trigger the prompt to download Apple's Java 6, which should do fine.

To get the idea:
-----------

#### Only tested on Mac OS X (for now):

    ~> ./aviary get all
    ~> make test ecj-jar=ecj.jar javah-jar=javah.jar use-lto=true lzma=lzma
    ~> make test ecj-jar=ecj.jar javah-jar=javah.jar use-lto=true lzma=lzma android=android test
    ~> make test ecj-jar=ecj.jar javah-jar=javah.jar use-lto=true lzma=lzma openjdk=$PWD/openjdk openjdk-src=$PWD/openjdk/src

Or reverse that order, put the binary at ```build/darwin-x86_64-lzma-bootimage-openjdk-src/avian``` somewhere in your path, and you should be able to compile the others without a JDK proper.

##### Quick Notes:
* ECJ seems to work beautifully as a drop-in replacement for javac.
* The javah jarfile is derived from the GNU classpath tools bundle generated in an intermediate step when compiling GCJ. I put in an gist.
* The patches applied to the JDK source are diff'ed from a hg checkout, since Oracle hasn't bothered to update their source bundle since releasing their latest ~~update~~ emergency security patch. These are also pulled from a gist.
* Everything is in the script. ```./aviary patch``` then diff if you just want to look at the changes to the makefile.
* All code using a different license is pulled in via curl; there's no dirty code in the script.
* There are three test failures; two in the OpenJDK build, and one in Android.
* Why do relative paths work for everything except ```openjdk=```?


Onward.