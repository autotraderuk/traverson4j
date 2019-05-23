package uk.co.autotrader.traverson.http;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * FormData body
 */
public class FormDataBody implements Body<FormDataBody.NameValuePair[]> {
    private final NameValuePair[] nameValuePairs;
    private final Charset charset;

    /**
     * Constructs a FormDataBody
     * @param charset see {@link Charset}
     * @param nameValuePairs see {@link NameValuePair}
     */
    public FormDataBody(Charset charset, NameValuePair... nameValuePairs) {
        this.nameValuePairs = Arrays.copyOf(nameValuePairs, nameValuePairs.length);
        this.charset = charset;
    }

    /**
     * @return the nameValuePairs that make up the request
     */
    @Override
    public NameValuePair[] getContent() {
        return Arrays.copyOf(nameValuePairs, nameValuePairs.length);
    }

    /**
     * The content type for this is null. This is so that the
     * underlying http client can generate it with the boundary
     * @return null
     */
    @Override
    public String getContentType() {
        return null;
    }

    /**
     * @return the charset for the request
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * A holder for a name value pair of the encapsulating FormData request
     */
    public static class NameValuePair {
        private final String name;
        private final String value;
        /**
         * Constructs a NameValuePair
         * @param name see {@link NameValuePair#getName()}
         * @param value see {@link NameValuePair#getValue()}
         */
        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        /**
         * This is good practice to use and some API's may look for specific form data elements by name
         * @return name of this form data field
         */
        public String getName() {
            return name;
        }

        /**
         * @return the value of this form data field
         */
        public String getValue() {
            return value;
        }
    }

}
