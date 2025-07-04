# Fisica Multimediale

This is the repository of the simulation software _Fisica Multimediale_ that
I was able to recover from the original CD-ROM.

The original software was written in 1997 and cannot run on today's JRE in Windows,
macOS or Linux operating systems without changes, so it can be considered as
abandonware. This repository is an effort to preserve it from being lost.
A pristine copy of it can be found at the initial import commit.


## Build and Execution

From the root of the project, to compile sources into .class files:

```shell
find -name "*.java" > /tmp/sources.txt
javac --add-exports java.desktop/java.awt.peer=ALL-UNNAMED -d /tmp/out @/tmp/sources.txt
```

To create single jar file:

```shell
cp -a --parents circuiti/icons/ icons/ help/ /tmp/out/
jar -cvf simul.jar -C /tmp/out/ .
rm -fr /tmp/sources.txt /tmp/out/
```

From the root of the project, to execute a module:

```shell
java -cp . cinemat.Cinemat
java -cp . dinam1.Dinam1
java -cp . dinam2.Dinam2
java -cp . onde.Onde
java -cp . oscill.Oscill
java -cp . ottica.Ottica
java -cp . gas.Gas
java -cp . elemag.EleMag
java -cp . relat.Relat
java -cp . fismod.FisMod
```

or alternatively, using the jar file:

```shell
java -cp simul.jar cinemat.Cinemat
java -cp simul.jar dinam1.Dinam1
java -cp simul.jar dinam2.Dinam2
java -cp simul.jar onde.Onde
java -cp simul.jar oscill.Oscill
java -cp simul.jar ottica.Ottica
java -cp simul.jar gas.Gas
java -cp simul.jar elemag.EleMag
java -cp simul.jar relat.Relat
java -cp simul.jar fismod.FisMod
```

## Changes applied

The source code requires JDK 1.1.x to compile and execute without any changes.
Execution on JRE 1.2 up to JRE 6 is possible only from already compiled code.


### 1. "Overriding problem" for get X/Y position


    double getX()
    double getY()


These are two methods from `elemag.CaricaDialog` and other similar classes that
will collision with `java.awt.Component` interface from Java 1.2 and onwards
because two similar methods have been introduced in `java.awt.Component` class:

    public int getX()
    public int getY()

Since the abovementioned methods have been created specifically for this project,
a solution is to rename them and avoid the method naming collision that occurs
at compile time.

    double getX()  >  double getXPos()
    double getY()  >  double getYPos()


### 2. Missing java.awt.peer.ComponentPeer.minimumSize()

`java.awt.peer.ComponentPeer.minimumSize()` has been deprecated since Java 1.1
in favor of `ComponentPeer.getMinimumSize()` and it has been removed in Java 7.
The simple solution is to call `getMinimumSize()` instead of the deprecated method.

    ComponentPeer.minimumSize()  >  ComponentPeer.getMinimumSize()

With this two changes, the source code can be compiled and executed with Java 8.


### 3. Missing java.awt.peer.ComponentPeer.getPeer()

Since Java 9 `java.awt.peer.ComponentPeer` class is not accessible and its
`getPeer()` method has been removed. All lines that call this method needs to be
commented or changed according to the guidelines:

* https://docs.oracle.com/en/java/javase/17/migrate/removed-apis.html#GUID-0C350BAB-F2C8-409E-AD3E-63831C684A55
* https://mail.openjdk.org/pipermail/awt-dev/2015-February/008924.html

With all these tree changes, the code can be compiled and executed on Java 9
and onwards.

### 4. Removing java.lang.Thread.stop() usage

Since Java 20 `java.lang.Thread.stop()` implementation throws
`UnsupportedOperationException` and it is maked for removal, so if the user tries
to exit the application before the animation ends, that exception is thrown.
To avoid that the code has been changed to not call `Thread.stop()` method, but
changing a `java.util.concurrent.atomic.AtomicBoolean` value to properly end the
animation.

It has been tested (compilation and execution) with the following JDK on Linux amd64:

```
JRE 1.1.8 executes original compiled version, but it goes in loop if a negative 
          charge is set in elemag.EleMag. This is because of a comment in     
          icons/neg.gif that causes some errors when decoding the file.
          The file has been fixed with another copy found in the project.
JRE 1.2   "
JRE 1.3   "
JRE 1.4   "
JRE 1.5   "
JRE 1.6   "
JDK  8  Eclispe Temurin  1.8.0_442
JDK 11  Eclispe Temurin 11.0.26     requires removal of java.awt.peer.ComponentPeer.getPeer()
JDK 17  OpenJDK-17      17.0.15
JDK 21  OpenJDK-21      21.0.7      requires patch of java.lang.Thread.stop()
                                    if someone wants to exit before the animation
                                    completes, otherwise it throws
                                    UnsupportedOperationException (since Java 20)
JDK 25  OpenJDK-25-ea   25-ea+22
```
