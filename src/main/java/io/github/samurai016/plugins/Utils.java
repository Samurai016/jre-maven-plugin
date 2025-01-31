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
package io.github.samurai016.plugins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.function.LongConsumer;

/**
 * Utility class
 *
 * @author Nicolò Rebaioli
 */
public abstract class Utils {
    /**
     * Convert an object to a map of JsonPath -> value
     * Example:
     * { "a": { "b": 1, "c": 2 } }
     * will be converted to:
     * { "a.b": 1, "a.c": 2 }
     *
     * @param obj Object to convert
     * @return Map of JsonPath -> value
     */
    public static Map<String, String> convertObjectToJsonPathMap(Object obj) {
        Map<String, String> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.valueToTree(obj);  // Convert object to JsonNode tree
        traverseJsonNode(root, "", result);
        return result;
    }

    private static void traverseJsonNode(JsonNode node, String path, Map<String, String> map) {
        if (node.isValueNode()) {
            map.put(path, node.asText());
        } else if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String currentPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();
                traverseJsonNode(field.getValue(), currentPath, map);
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                traverseJsonNode(node.get(i), path + "[" + i + "]", map);
            }
        }
    }

    /**
     * Create a BodyHandler that calls a callback every interval bytes received
     *
     * @param interval Interval in bytes
     * @param callback Callback to call
     * @param h BodyHandler to wrap
     * @return Wrapped BodyHandler
     * @param <T> Type of the response body
     */
    public static <T> HttpResponse.BodyHandler<T> callbackBodyHandler(int interval, LongConsumer callback, HttpResponse.BodyHandler<T> h) {
        return info -> new HttpResponse.BodySubscriber<>() {
            private final HttpResponse.BodySubscriber<T> delegateSubscriber = h.apply(info);
            private long receivedBytes = 0;
            private long calledBytes = 0;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                delegateSubscriber.onSubscribe(subscription);
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
                receivedBytes += item.stream().mapToLong(ByteBuffer::capacity).sum();

                if (receivedBytes - calledBytes > interval) {
                    callback.accept(receivedBytes);
                    calledBytes = receivedBytes;
                }

                delegateSubscriber.onNext(item);
            }

            @Override
            public void onError(Throwable throwable) {
                delegateSubscriber.onError(throwable);

            }

            @Override
            public void onComplete() {
                delegateSubscriber.onComplete();
            }

            @Override
            public CompletionStage<T> getBody() {
                return delegateSubscriber.getBody();
            }
        };
    }

    /**
     * Convert bytes to human-readable format (1024-based)
     *
     * @param bytes Bytes to convert
     * @return Human-readable format
     */
    public static String bytesToHuman(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f%cB", value / 1024.0, ci.current());
    }

    /**
     * Check if a path is valid
     *
     * @param path Path to check
     * @return True if the path is valid, false otherwise
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "ResultOfMethodCallIgnored"})
    public static boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        try {
            Path.of(path);
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }
}
