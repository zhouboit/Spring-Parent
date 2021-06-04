package com.jonbore.clickhouse;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * 解析 main方法输入参数 适配-arg or --arg
 *
 * @author bo.zhou
 */
public class Option {
    private Hashtable<String, String> options = new Hashtable<>();

    public String get(String key) {
        return options.get(key);
    }

    public boolean has(String key) {
        return options.containsKey(key);
    }

    private Option() {
    }

    private Option(Hashtable<String, String> params) {
        this.options = params;
    }

    private static String getKeyFromArgs(String[] args, int index) {
        String key;
        if (args[index].startsWith("--")) {
            key = args[index].substring(2);
        } else if (args[index].startsWith("-")) {
            key = args[index].substring(1);
        } else {
            throw new IllegalArgumentException(
                    String.format(
                            "Error parsing arguments '%s' on '%s'. Please prefix keys with -- or -.",
                            Arrays.toString(args), args[index]));
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException(
                    "The input " + Arrays.toString(args) + " contains an empty argument");
        }

        return key;
    }

    private static final String NO_VALUE_KEY = "__NO_VALUE_KEY";

    public static Option fromArgs(String[] args) {
        final Hashtable<String, String> map = new Hashtable<>();
        int i = 0;
        while (i < args.length) {
            final String key = getKeyFromArgs(args, i);

            i += 1; // try to find the value
            if (i >= args.length) {
                map.put(key, NO_VALUE_KEY);
            } else if (args[i].startsWith("--") || args[i].startsWith("-")) {
                // the argument cannot be a negative number because we checked earlier
                // -> the next argument is a parameter name
                map.put(key, NO_VALUE_KEY);
            } else {
                map.put(key, emptyObj(args[i]));
                i += 1;
            }
        }
        return new Option(map);
    }

    private static String emptyObj(String string) {
        if (string == null
                || string.trim().isEmpty()
                || string.equalsIgnoreCase("null")
        ) {
            return null;
        }
        return string;
    }
}
