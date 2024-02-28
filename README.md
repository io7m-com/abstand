abstand
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.abstand/com.io7m.abstand.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.abstand%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.abstand/com.io7m.abstand?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/abstand/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m/abstand.svg?style=flat-square)](https://codecov.io/gh/io7m/abstand)

![com.io7m.abstand](./src/site/resources/abstand.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m/abstand/main.linux.temurin.current.yml)](https://github.com/io7m/abstand/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m/abstand/main.linux.temurin.lts.yml)](https://github.com/io7m/abstand/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m/abstand/main.windows.temurin.current.yml)](https://github.com/io7m/abstand/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m/abstand/main.windows.temurin.lts.yml)](https://github.com/io7m/abstand/actions?query=workflow%3Amain.windows.temurin.lts)|

## Abstand

A simple, correct, efficient [interval tree](https://en.wikipedia.org/wiki/Interval_tree) implementation.

## Motivation

At the time of writing, no interval tree implementations exist for Java
that have all of the following properties:

* Simple, readable, and well-commented.
* Not part of an existing massive, poor-quality library such as Guava.
* Heavily tested with an exhaustive test suite.
* Liberally licensed.
* Published to Maven Central.
* [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System)-ready.
* [OSGi](https://www.osgi.org/)-ready.

The `abstand` package provides a generic interval tree implementation based
on an [AVL tree](https://en.wikipedia.org/wiki/AVL_tree) that aims to meet
all of the above requirements.

## Usage

Implementations of the `IntervalTreeType` are implementations of `Set` that
also allow for overlapping queries:

```
var t = IntervalTree.<Long>create();
t.add(IntervalL.of(20, 30));
t.add(IntervalL.of(25, 30));

var o = t.overlapping(IntervalL.of(26, 28));
 // o == [[20, 30], [25, 30]];
```

Interval trees contain values of type `IntervalTreeType<S>` for some scalar
type `S`. The following implementations are provided:

|Type       |Description                             |
|-----------|----------------------------------------|
|`IntervalD`|Intervals with `double`-typed values    |
|`IntervalB`|Intervals with `BigInteger`-typed values|
|`IntervalL`|Intervals with `long`-typed values      |
|`IntervalI`|Intervals with `int`-typed values       |

## Notes

Credit is given to someone named "John Hargrove" who published what appears to
be the only comprehensible explanation of AVL tree rotations online. All other
texts appear to contain subtle mistakes, or miss the exact conditions required
for each rotation type to be applicable.

He originally published a [document](https://www.cise.ufl.edu/~nemo/cop3530/AVL-Tree-Rotations.pdf)
online, but I've included a copy in the [references](references/AVL-Tree-Rotations.pdf)
subdirectory as I do not trust random files published in the home directories
of computer science students on university servers to stay accessible.

