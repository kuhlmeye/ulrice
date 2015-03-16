package net.ulrice.webstarter.util;

public class WebstarterUtils {

    public static interface PlaceholderResolver {
        String resolve(String key);
    }

    /**
     * The prefix for entities (${)
     */
    public static final String ENTITY_PREFIX = "${";

    /**
     * The postfix for entities (})
     */
    public static final String ENTITY_POSTFIX = "}";

    /**
     * Replaces all placeholders like ${...} by using System.properties and environment variables
     * 
     * @param value the value
     * @return the value with resolved placehodlers
     */
    public static String resolvePlaceholders(String value) {
        return resolvePlaceholders(value, new PlaceholderResolver() {

            @Override
            public String resolve(String key) {
                String suffix = null;
                int index = key.lastIndexOf(':');

                if (index >= 0) {
                    suffix = key.substring(index + 1);
                    key = key.substring(0, index);
                }

                String value = System.getProperty(key);

                if (value == null) {
                    value = System.getenv(key);
                }

                if (value == null) {
                    System.err.println("Failed to resoulve placeholder ${" + key + "}");
                }

                return handleSuffix(value, suffix);
            }

            private String handleSuffix(String value, String suffix) {
                if (value == null) {
                    return null;
                }

                value = value.replace('\\', '/');

                if ((suffix == null) || (value.endsWith(suffix))) {
                    return value;
                }

                return value + suffix;
            }
        });
    }

    /**
     * Replaces all placeholders like ${...} by using the specified resolver
     * 
     * @param value the value
     * @param resolver the resolver
     * @return the value with resolved placehodlers
     */
    public static String resolvePlaceholders(String value, PlaceholderResolver resolver) {
        if (value == null) {
            return null;
        }

        int beginIndex = value.indexOf(ENTITY_PREFIX);

        if (beginIndex < 0) {
            return value;
        }

        int endIndex = value.indexOf(ENTITY_POSTFIX, beginIndex);

        if (endIndex < 0) {
            return value;
        }

        StringBuilder result = new StringBuilder();
        int currentIndex = 0;

        while (currentIndex < value.length()) {
            if ((beginIndex - currentIndex) > 0) {
                result.append(value.substring(currentIndex, beginIndex));
            }

            String key = value.substring(beginIndex + ENTITY_PREFIX.length(), endIndex);
            String resolvedValue = resolver.resolve(key);

            if (resolvedValue == null) {
                result.append(ENTITY_PREFIX + key + ENTITY_POSTFIX);
            }
            else {
                result.append(resolvedValue);
            }

            currentIndex = endIndex + 1;

            if (currentIndex < value.length()) {
                beginIndex = value.indexOf(ENTITY_PREFIX, currentIndex);

                if (beginIndex < 0) {
                    if (currentIndex < value.length()) {
                        result.append(value.substring(currentIndex));
                    }

                    break;
                }

                endIndex = value.indexOf(ENTITY_POSTFIX, beginIndex);

                if (endIndex < 0) {
                    if (currentIndex < value.length()) {
                        result.append(value.substring(currentIndex));
                    }

                    break;
                }
            }
        }

        return result.toString();
    }

}
