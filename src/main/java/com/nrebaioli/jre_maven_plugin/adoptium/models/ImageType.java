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
package com.nrebaioli.jre_maven_plugin.adoptium.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ImageType enum<br>
 * See <code>ImageType</code> model at <a href="https://api.adoptium.net/q/swagger-ui/">Adoptium API</a>
 *
 * @author Nicolò Rebaioli
 */
public enum ImageType {
    jdk,
    jre,
    testimage,
    debugimage,
    staticlibs,
    sources,
    sbom;

    @JsonValue
    public String getJsonValue() {
        return this.name().toLowerCase();
    }
}
