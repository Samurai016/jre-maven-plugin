# JRE Maven Plugin
[![Latest Version](https://img.shields.io/github/v/release/Samurai016/jre-maven-plugin?style=flat-square&logo=github&label=latest)](https://github.com/Samurai016/jre-maven-plugin/releases/latest)
[![GitHub Packages](https://img.shields.io/badge/GitHub%20Packages-%23181717?style=flat-square&logo=github)](https://github.com/Samurai016/jre-maven-plugin/packages/2387736)
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
                <version>17</version>
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
    <version>17</version>
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
If you encounter any problems, feel free to [open an issue](https://github.com/your-repo/issues).

## ❤️ Credits
Thanks to [Adoptium](https://adoptium.net/) for providing reliable JRE builds.

[adoptium-docs]: https://api.adoptium.net/q/swagger-ui/