/*
 * Maven JRE Plugin
 * Copyright (c) 2025 Nicolò Rebaioli
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.nrebaioli.jre_maven_plugin;

import com.nrebaioli.jre_maven_plugin.adoptium.AdoptiumApi;
import com.nrebaioli.jre_maven_plugin.adoptium.models.*;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Bundles a JRE inside the project.<br>
 * It can download the JRE and (optionally) unzip it to a specified directory.
 *
 * @author Nicolò Rebaioli
 */
@Mojo(name = "jre-bundler", defaultPhase = LifecyclePhase.PACKAGE)
@SuppressWarnings("ResultOfMethodCallIgnored")
public class JreBundlerMojo extends AbstractMojo {
    /**
     * The version of the JRE to download.<br>
     * If not specified, it will use the Maven compiler source version.<br>
     * It should be a valid Java feature release (e.g. 8, 11, 16).
     */
    @Parameter(defaultValue = "${maven.compiler.source}")
    int version;

    /**
     * The JVM implementation to download.<br>
     * Currently, only hotspot is supported.
     */
    @Parameter(defaultValue = "hotspot")
    JVMImpl jvmImpl;

    /**
     * The architecture of the JRE to download.
     */
    @Parameter(defaultValue = "x64")
    Architecture architecture;

    /**
     * The operating system of the JRE to download.
     */
    @Parameter(defaultValue = "windows")
    OperatingSystem os;

    /**
     * The type of image to download.<br>
     * It is suggested to use the jre image type (it is the smallest one).
     */
    @Parameter(defaultValue = "jre")
    ImageType imagetype;

    /**
     * The vendor of the JRE to download.<br>
     * Currently, only Eclipse is supported.
     */
    @Parameter(defaultValue = "eclipse")
    Vendor vendor;

    /**
     * The directory where the JRE will be downloaded.<br>
     * If not specified, it will use the project build directory.
     * <p>
     * The path can contain variables in the form <code>{{variable}}</code>.<br>
     * See [the documentation]() for the list of available variables.
     */
    @Parameter(defaultValue = "${project.build.directory}/jre")
    String outputdir;

    /**
     * The name of the output file.<br>
     * If not specified, it will use the release name of the JRE.<br>
     * If the name does not end with ".zip", it will be appended.
     * <p>
     * The path can contain variables in the form <code>{{variable}}</code>.<br>
     * See the documentation for the list of available variables.
     */
    @Parameter()
    String outputfilename;

    /**
     * The directory where the JRE will be unzipped.<br>
     * If not specified, the JRE will not be unzipped.
     * <p>
     * The path can contain variables in the form <code>{{variable}}</code>.<br>
     * See the documentation for the list of available variables.
     */
    @Parameter()
    String unzipto;

    /**
     * If true, the contents of the first-level folder in the JRE zip will be moved to the root.<br>
     * If false, the contents will be extracted in the first-level folder.
     * <p>
     * e.g.<br>
     * * <b>unzipto</b> = <code>{{project.build.directory}}/jre</code><br>
     * * <b>movetoroot</b> = <code>true</code><br>
     * * <b>JRE zip contents</b>: <code>jdk-16.0.1/bin</code>, <code>jdk-16.0.1/lib</code>, ...<br>
     * * <b>Output</b>: <code>{{project.build.directory}}/jre/bin</code>, <code>{{project.build.directory}}/jre/lib</code>, ...<br>
     * <p>
     * e.g.<br>
     * * <b>unzipto</b> = <code>{{project.build.directory}}/jre</code><br>
     * * <b>movetoroot</b> = <code>false</code><br>
     * * <b>JRE zip contents</b>: <code>jdk-16.0.1/bin</code>, <code>jdk-16.0.1/lib</code>, ...<br>
     * * <b>Output</b>: <code>{{project.build.directory}}/jre/jdk-16.0.1/bin</code>, <code>{{project.build.directory}}/jre/jdk-16.0.1/lib</code>, ...
     */
    @Parameter(defaultValue = "true")
    boolean movetoroot;

    /**
     * Move the contents of the first-level folder in the archive to the root.
     *
     * @param folder The folder which contains the contents to move
     * @param destination The destination directory
     */
    private static void moveDirectoryContent(FileHeader folder, File destination) {
        File folderPath = new File(destination, folder.getFileName());
        File[] files = folderPath.listFiles();
        if (files != null) {
            for (File f : files) {
                f.renameTo(new File(destination, f.getName()));
            }
        }
        folderPath.delete();
    }

    @Override
    public void execute() throws MojoExecutionException {
        try {
            // Print the parameters
            getLog().info("JRE Bundler Plugin");

            getLog().info("Version: " + version);
            getLog().info("JVM Implementation: " + jvmImpl);
            getLog().info("Architecture: " + architecture);
            getLog().info("OS: " + os);
            getLog().info("Image Type: " + imagetype);
            getLog().info("Vendor: " + vendor);
            getLog().info("Output directory: " + outputdir);
            getLog().info("Output filename: " + outputfilename);
            getLog().info("Unzip to: " + unzipto);

            // Check parameters
            checkParameters();

            // Get the JRE from Adoptium
            AdoptiumApi api = new AdoptiumApi();
            Release[] versions = api.getLatestVersion(version, jvmImpl, architecture, imagetype, os, vendor);
            if (versions.length == 0) {
                throw new MojoExecutionException("No versions found");
            }
            getLog().info("Found " + versions.length + " versions");
            Release chosenVersion = versions[0];

            // Generate the output path
            if (outputfilename == null || outputfilename.isEmpty()) {
                outputfilename = chosenVersion.release_name;
            }
            if (!outputfilename.endsWith(".zip")) {
                outputfilename += ".zip";
            }
            outputdir = generatePath(outputdir, chosenVersion);
            outputfilename = generatePath(outputfilename, chosenVersion);

            // Download the JRE
            File destination = Path.of(outputdir, outputfilename).toFile();
            getLog().info("Downloading " + chosenVersion.release_name + " from " + chosenVersion.binary.pkg.link);
            download(new URI(chosenVersion.binary.pkg.link), destination, chosenVersion.binary.pkg.size);

            // If the unzip configuration is set, unzip the JRE
            if (unzipto != null && !unzipto.isEmpty()) {
                getLog().info("Unzipping " + destination);
                unzipFile(destination, unzipto);
                destination.delete();
            }
        } catch (Throwable e) {
            throw new MojoExecutionException(e);
        }
    }

    /**
     * Check the validity of the parameters.<br>
     * If the parameters are not valid, it throws a MojoExecutionException.
     *
     * @throws MojoExecutionException If the parameters are not valid
     */
    private void checkParameters() throws MojoExecutionException {
        if (unzipto != null && !unzipto.isEmpty() && !Utils.isValidPath(unzipto)) {
            throw new MojoExecutionException("Invalid unzip path: " + unzipto);
        }
        if (outputdir != null && !outputdir.isEmpty() && !Utils.isValidPath(outputdir)) {
            throw new MojoExecutionException("Invalid output directory: " + outputdir);
        }
        if (outputfilename != null && !outputfilename.isEmpty() && outputfilename.endsWith("/")) {
            throw new MojoExecutionException("Output filename cannot be a directory");
        }
    }

    /**
     * Download a file from a URI to a destination file.
     *
     * @param uri The URI of the file to download
     * @param destination The destination file
     * @param fileSize The size of the file to download
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the download is interrupted
     */
    private void download(URI uri, File destination, int fileSize) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()) {
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

            // Create destination directory
            File parent = destination.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            HttpResponse.BodyHandler<Path> handler = Utils.callbackBodyHandler(
                    1024 * 1024, // 1MB
                    (receivedBytes) -> getLog().info(String.format("Downloaded %s/%s bytes", Utils.bytesToHuman(receivedBytes), Utils.bytesToHuman(fileSize))),
                    HttpResponse.BodyHandlers.ofFile(destination.toPath())
            );
            HttpResponse<Path> response = client.send(request, handler);
            if (response.statusCode() != 200) {
                throw new HttpResponseException(response.statusCode(), response.body().toString());
            }

            getLog().info("Downloaded " + destination);
        }
    }

    /**
     * Unzip a file to a specified directory.
     *
     * @param file The file to unzip
     * @param unzipto The directory where to unzip the file
     * @throws IOException If an I/O error occurs
     */
    private void unzipFile(File file, String unzipto) throws IOException {
        // Create destination directory
        String outputDir = generatePath(unzipto, new Release());
        File destination = new File(outputDir);
        if (!destination.exists()) {
            destination.mkdirs();
        }

        try (ZipFile archive = new ZipFile(file)) {
            // Extract the archive
            archive.extractAll(outputDir);

            // If the movetoroot configuration is set, move the contents to the root
            if (movetoroot) {
                // Get the number of first-level folders in the archive
                List<FileHeader> firstLevelFolders = archive.getFileHeaders().stream()
                        .filter(header -> header.isDirectory() && (StringUtils.countMatches(header.getFileName(), "/") == 1 || StringUtils.countMatches(header.getFileName(), "\\") == 1))
                        .toList();

                // If there is only one first-level folder, move its contents to the root
                if (firstLevelFolders.size() <= 1) {
                    moveDirectoryContent(firstLevelFolders.getFirst(), destination);
                }
            }

            getLog().info("Unzipped " + file);
        }
    }

    /**
     * Generate a path by replacing the variables with the values in the version.
     *
     * @param path The path with variables
     * @param version The version to use for the replacement
     * @return The path with the variables replaced
     */
    private String generatePath(String path, Release version) {
        Map<String, String> map = Utils.convertObjectToJsonPathMap(version);
        return StringSubstitutor.replace(path, map, "{{", "}}");  // Replace all the variables in the path
    }
}
