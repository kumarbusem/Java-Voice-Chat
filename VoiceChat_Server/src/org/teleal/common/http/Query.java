package org.teleal.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Taken from http://stackoverflow.com/questions/1667278/parsing-query-strings-in-java
 *
 * @author Christian Bauer
 */
public class Query {

    private String qs = "";
    private Map<String, List<String>> parameters = new TreeMap<String, List<String>>();

    public static String parseParameters(Map<String, String[]> parameters) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            for (String v : entry.getValue()) {
                if (v == null || v.length() == 0) continue;
                if (sb.length() > 0) sb.append("&");
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(v);
            }
        }
        return sb.toString();

    }

    public Query() {
    }

    public Query(Map<String, String[]> parameters) {
        this(parseParameters(parameters));
    }

    public Query(URL url) {
        this(url.getQuery());
    }

    public Query(String qs) {
        if (qs == null) return;
        this.qs = qs;

        // Parse query string
        String pairs[] = qs.split("&");
        for (String pair : pairs) {
            String name;
            String value;
            int pos = pair.indexOf('=');
            // for "n=", the value is "", for "n", the value is null
            if (pos == -1) {
                name = pair;
                value = null;
            } else {
                try {
                    name = URLDecoder.decode(pair.substring(0, pos), "UTF-8");
                    value = URLDecoder.decode(pair.substring(pos + 1, pair.length()), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // Not really possible, throw unchecked
                    throw new IllegalStateException("Query string is not UTF-8");
                }
            }
            List<String> list = parameters.get(name);
            if (list == null) {
                list = new ArrayList<String>();
                parameters.put(name, list);
            }
            list.add(value);
        }
    }

    public String get(String name) {
        List<String> values = parameters.get(name);
        if (values == null)
            return "";

        if (values.size() == 0)
            return "";

        return values.get(0);
    }

    public String[] getValues(String name) {
        List<String> values = parameters.get(name);
        if (values == null)
            return null;

        return values.toArray(new String[values.size()]);
    }

    public Enumeration<String> getNames() {
        return Collections.enumeration(parameters.keySet());
    }

    public Map<String, String[]> getMap() {
        Map<String, String[]> map = new TreeMap<String, String[]>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            List<String> list = entry.getValue();
            String[] values;
            if (list == null)
                values = null;
            else
                values = list.toArray(new String[list.size()]);
            map.put(entry.getKey(), values);
        }
        return map;
    }

    public boolean isEmpty() {
        return parameters.size() == 0;
    }

    @Override
    public String toString() {
        return qs;
    }
}