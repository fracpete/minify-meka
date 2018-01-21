# minify-meka
Tool for reducing Meka to a minimal subset of classes.

## Command-line help
When using -h or --help you get the following help screen:

```
usage: com.github.fracpete.minify.Meka
       [-h] --java-home JAVAHOME --classes CLASSES
       [--additional ADDITIONAL] --input INPUT --output OUTPUT [--test]
       packages [packages ...]

positional arguments:
  packages               The packages to keep, eg 'meka'.

optional arguments:
  -h, --help             show this help message and exit
  --java-home JAVAHOME   The java home directory  of  the JDK that includes
                         the jdeps binary, default  is taken from JAVA_HOME
                         environment variable.
  --classes CLASSES      The file containing the  classes  to determine the
                         dependencies for. Empty  lines  and lines starting
                         with # get ignored.
  --additional ADDITIONAL
                         The file  with  additional  class  names  to  just
                         include.
  --input INPUT          The directory with the  pristing build environment
                         in.
  --output OUTPUT        The  directory  for  storing  the  minified  build
                         environment in.
  --test                 Optional   testing   of    the    minified   build
                         environment.
```

## Example

Classes to include (`classes.txt`):

```
meka.classifiers.multilabel.BR
```

Command-line for generating a minified version of Meka:
```bash
java com.github.fracpete.minify.Meka
  --java-home /somepath/jdk/jdk1.8.0_144-64bit \
  --input /someplace/meka/ \
  --output /elsewhere/mekaout/ \
  --classes /elsewhere/classes.txt \
  meka
```

**Note:** When compiling the minified version, either delete the 
`maven-exec-plugin` build tag or use `-Dexec.skip=True`

## Maven
Use the following dependency in your `pom.xml`:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>minify-meka</artifactId>
      <version>0.0.3</version>
    </dependency>
```

## Releases

The `bin` directory of the zip file contains a batch file and a bash script
for making execution easier.

* [0.0.3](https://github.com/fracpete/minify-meka/releases/download/minify-meka-0.0.3/minify-meka-0.0.3-bin.zip)
* [0.0.2](https://github.com/fracpete/minify-meka/releases/download/minify-meka-0.0.2/minify-meka-0.0.2-bin.zip)
* [0.0.1](https://github.com/fracpete/minify-meka/releases/download/minify-meka-0.0.1/minify-meka-0.0.1-bin.zip)
