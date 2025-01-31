/*
 * Maven JRE Plugin
 * Copyright (c) 2025 Nicolò Rebaioli
 *
 * This file is part of Maven JRE Plugin.
 *
 * Maven JRE Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Maven JRE Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Maven JRE Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.samurai016.plugins.adoptium;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.samurai016.plugins.adoptium.models.*;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Adoptium API client<br>
 * Used to interact with the Adoptium API
 *
 * @author Nicolò Rebaioli
 */
public class AdoptiumApi {
    private static final String BASE_URL = "https://api.adoptium.net";

    /**
     * Get the latest version of a given feature version
     *
     * @param featureVersion The feature version to get the latest version of
     * @param jvmImpl The JVM implementation
     * @param architecture The architecture
     * @param imageType The image type
     * @param os The operating system
     * @param vendor The vendor
     * @return An array of VersionResult objects
     * @throws IOException If an error occurs while making the request
     * @throws InterruptedException If the request is interrupted
     */
    public Release[] getLatestVersion(int featureVersion, JVMImpl jvmImpl, Architecture architecture, ImageType imageType, OperatingSystem os, Vendor vendor) throws IOException, InterruptedException {
        String url;
        try {
            url = new URIBuilder(BASE_URL)
                    .setPath("/v3/assets/latest/" + featureVersion + "/" + jvmImpl.getJsonValue())
                    .addParameter("architecture", architecture.getJsonValue())
                    .addParameter("image_type", imageType.getJsonValue())
                    .addParameter("os", os.getJsonValue())
                    .addParameter("vendor", vendor.getJsonValue())
                    .toString();
        } catch (URISyntaxException e) {
            // This should never happen
            throw new RuntimeException(e);
        }

        return get(url, Release[].class);
    }

    /**
     * Make a GET request
     * @param url URL to make the request to
     * @param clazz Class to parse the response to
     * @return The parsed response
     */
    @SuppressWarnings("SameParameterValue")
    private <T> T get(String url, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return request(request, clazz);
    }

    /**
     * Make a request
     * @param request Request to make
     * @param clazz Class to parse the response to
     * @return The parsed response
     */
    private <T> T request(HttpRequest request, Class<T> clazz) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new HttpResponseException(response.statusCode(), response.body());
            }

            String json = response.body();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        }
    }
}
