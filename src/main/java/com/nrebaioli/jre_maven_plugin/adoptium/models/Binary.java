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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Binary model<br>
 * See <code>Binary</code> model at <a href="https://api.adoptium.net/q/swagger-ui/">Adoptium API</a>
 *
 * @author Nicolò Rebaioli
 */
@SuppressWarnings("unused")
public class Binary {
    public String architecture;
    public int download_count;
    public String heap_size;
    public String image_type;
    public Installer installer;
    public String jvm_impl;
    public String os;
    @JsonProperty("package")
    public Package pkg;
    public String project;
    public String scm_ref;
    public Date updated_at;
}
