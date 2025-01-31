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
package io.github.samurai016.jre_maven_plugin.adoptium.models;

/**
 * Version model<br>
 * See <code>Version</code> model at <a href="https://api.adoptium.net/q/swagger-ui/">Adoptium API</a>
 *
 * @author Nicolò Rebaioli
 */
@SuppressWarnings("unused")
public class Version {
    public int build;
    public int major;
    public int minor;
    public String openjdk_version;
    public String optional;
    public int security;
    public String semver;
    public int patch;
    public int adopt_build_numberinteger;
}
