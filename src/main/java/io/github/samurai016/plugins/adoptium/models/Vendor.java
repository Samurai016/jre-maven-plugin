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
package io.github.samurai016.plugins.adoptium.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Vendor enum<br>
 * See <code>Vendor</code> model at <a href="https://api.adoptium.net/q/swagger-ui/">Adoptium API</a>
 *
 * @author Nicolò Rebaioli
 */
public enum Vendor {
    eclipse;

    @JsonValue
    public String getJsonValue() {
        return this.name().toLowerCase();
    }
}
