# JRE Maven Plugin
[![Latest Version](https://img.shields.io/github/v/release/Samurai016/jre-maven-plugin?style=flat-square&logo=github&label=latest)](https://github.com/Samurai016/jre-maven-plugin/releases/latest)
[![Static Badge](https://img.shields.io/badge/Maven%20Central-%23161b37?style=flat-square&logo=sonatype&logoColor=%23f18900)](https://mvnrepository.com/artifact/io.github.samurai016.plugins/jre-maven-plugin)
[![Adoptium API](https://img.shields.io/badge/Adoptium%20API-%2314003c?style=flat-square&logo=eclipseadoptium&logoColor=%23ff1464&labelColor=%2314003c)](https://adoptium.net/)

This Maven plugin allows you to bundle a JRE inside your project.  
It can download the JRE from [Adoptium](https://adoptium.net/) and optionally unzip it to a specified directory.

## 🚀 Usage

Add the following to your `pom.xml` to use the plugin:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.github.samurai016.plugins</groupId>
            <artifactId>jre-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>jre-bundler</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <!-- Optional: Configure the parameters as needed -->
                <version>21</version>
                <outputdir>${project.build.directory}/bundled-jre</outputdir>
                <unzipto>${project.build.directory}/final-jre</unzipto>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 🔧 Plugin Parameters

| Parameter        | Default Value                    | Description                                                                                                                                                                      |
|------------------|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `version`        | `${maven.compiler.source}`       | The version of the JRE to download. Specify a valid Java feature version (e.g., 8, 11, 17, 21).                                                                                  |
| `jvmImpl`        | `hotspot`                        | The JVM implementation to download. Currently, only `hotspot` is supported. [See Adoptium V3 API docs for more details][adoptium-docs]                                           |
| `architecture`   | `x64`                            | The architecture of the JRE to download (e.g., `x64`, `arm64`). [See Adoptium V3 API docs for more details][adoptium-docs]                                                       |
| `os`             | `windows`                        | The operating system for the JRE (e.g., `windows`, `linux`, `mac`). [See Adoptium V3 API docs for more details][adoptium-docs]                                                   |
| `imagetype`      | `jre`                            | The type of image to download. It is recommended to use the `jre` image type as it is the smallest version available. [See Adoptium V3 API docs for more details][adoptium-docs] |
| `vendor`         | `eclipse`                        | The vendor providing the JRE. Currently, only `eclipse` (Adoptium) is supported. [See Adoptium V3 API docs for more details][adoptium-docs]                                      |
| `outputdir`      | `${project.build.directory}/jre` | The directory where the JRE will be downloaded. You can use variables in the form `{{variable}}`.                                                                                |
| `outputfilename` | `<release_name>.zip`             | The name of the output file. If not specified, it will use the release name of the JRE with `.zip` appended if necessary.                                                        |
| `unzipto`        | N/A                              | The directory where the JRE will be unzipped. If not specified, the JRE will not be unzipped.                                                                                    |
| `movetoroot`     | `true`                           | If `true`, the contents of the first-level folder in the JRE zip will be moved to the root directory. If `false`, they will stay nested.                                         |

## 🛠️ Example Configuration

### Basic Example
```xml
<configuration>
    <version>21</version>
    <!-- <version>${maven.compiler.source}</version> Uncomment this to use the same version as the project -->
    <outputdir>${project.build.directory}/bundled-jre</outputdir>
    <unzipto>${project.build.directory}/unpacked-jre</unzipto>
    <movetoroot>true</movetoroot>
</configuration>
```

### Advanced Example
In this example, we specify all possible configuration options:

```xml
<configuration>
    <version>21</version>
    <jvmImpl>hotspot</jvmImpl>
    <architecture>x64</architecture>
    <os>linux</os>
    <imagetype>jre</imagetype>
    <vendor>eclipse</vendor>
    <outputdir>${project.build.directory}/bundled-jre</outputdir>
    <outputfilename>jre.zip</outputfilename>
    <unzipto>${project.build.directory}/jre</unzipto>
    <movetoroot>true</movetoroot>
</configuration>
```
### Real World Example
In this example, we use the same JRE version as the project and we use:  

* **jre-maven-plugin** to bundle the JRE.
* [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/) to create a fat JAR (with dependencies).
* [launch4j-maven-plugin](https://github.com/orphan-oss/launch4j-maven-plugin) to create an executable file for Windows which includes the JRE.  

In this way, we can create a standalone executable file that runs on Windows without requiring the user to have Java installed or to update any existing Java installations.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>io.github.samurai016.plugins.example</groupId>
    <artifactId>jre-maven-plugin-example</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <app.mainClass>io.github.samurai016.plugins.example.App</app.mainClass>
    </properties>

    <build>
        <plugins>
            <!-- Maven JRE Plugin: Bundle JRE -->
            <plugin>
                <groupId>io.github.samurai016.plugins</groupId>
                <artifactId>jre-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>jre-bundler</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <version>${maven.compiler.release}</version>
                    <architecture>x64</architecture>
                    <imagetype>jre</imagetype>
                    <os>windows</os>
                    <unzipto>target/jre</unzipto>
                </configuration>
            </plugin>

            <!-- Maven Shade Plugin: Create a single executable jar -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>${app.mainClass}</mainClass>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Launch4j Maven Plugin: Create a Windows executable -->
            <plugin>
                <groupId>com.akathist.maven.plugins.launch4j</groupId>
                <artifactId>launch4j-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>l4j-clui</id>
                        <phase>package</phase>
                        <goals>
                            <goal>launch4j</goal>
                        </goals>
                        <configuration>
                            <headerType>console</headerType>
                            <outfile>target/${project.artifactId}.exe</outfile>
                            <jar>target/${project.artifactId}.jar</jar>
                            <!-- Other launch4j configuration options -->
                            <jre>
                                <path>./jre/${jre.version};%JAVA_HOME%;%PATH%</path>
                            </jre>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```
 


## 🔍 How It Works
1. **Parameter Configuration:** The plugin reads the configuration parameters specified in `pom.xml`.
2. **Adoptium API Interaction:** It retrieves the latest available JRE version from Adoptium.
3. **Download:** The JRE binary is downloaded to the specified output directory.
4. **Unzip (Optional):** If the `unzipto` parameter is set, the JRE is extracted to the given directory.
5. **Move to Root (Optional):** If `movetoroot` is `true`, the contents of the first-level folder in the archive are moved to the root.

## 📌️ Example Directory Structure

With the following configuration:
```xml
<outputdir>${project.build.directory}/bundled-jre</outputdir>
<outputfilename>jre.zip</outputfilename>
<unzipto>${project.build.directory}/jre</unzipto>
<movetoroot>true</movetoroot>
```
The result will be:
```
/target/bundled-jre
├── jre.zip
/target/jre
├── bin/
├── lib/
├── ...
```

## 💻 Development
To build and test the plugin locally, run:
```sh
mvn clean install
```

## 🛡️ License
This project is licensed under the [GNU General Public License v3.0](https://github.com/Samurai016/jre-maven-plugin/LICENSE.md).

## 🤝 Contributing
Contributions are welcome! Please fork the repository and submit a pull request.

## 🐛 Issues
If you encounter any problems, feel free to [open an issue](https://github.com/Samurai016/jre-maven-plugin/issues).

## ❤️ Credits
Thanks to [Adoptium](https://adoptium.net/) for providing reliable JRE builds.

[adoptium-docs]: https://api.adoptium.net/q/swagger-ui/