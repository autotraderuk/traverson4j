package uk.co.autotrader.traverson.http;

import java.io.InputStream;
import java.util.Arrays;

/**
 * Simple Multipart body
 */
public class SimpleMultipartBody implements Body<SimpleMultipartBody.BodyPart[]> {
    private final BodyPart[] bodyParts;

    /**
     * Constructs a SimpleMultipartBody
     * @param bodyParts see {@link BodyPart}
     */
    public SimpleMultipartBody(BodyPart... bodyParts) {
        this.bodyParts = Arrays.copyOf(bodyParts, bodyParts.length);
    }

    /**
     * @return the bodyParts that make up the request
     */
    @Override
    public BodyPart[] getContent() {
        return Arrays.copyOf(bodyParts, bodyParts.length);
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
     * A holder for a single body part of the encapsulating Multipart request
     */
    public static class BodyPart {
        private final String name;
        private final byte[] data;
        private final String value;
        private final InputStream inputStream;
        private final String contentType;
        private final String filename;

        /**
         * Constructs a BodyPart
         * @param name see {@link BodyPart#getName()}
         * @param data see {@link BodyPart#getData()}
         * @param contentType see {@link BodyPart#getContentType()}
         * @param filename see {@link BodyPart#getFilename()}
         */
        public BodyPart(String name, byte[] data, String contentType, String filename) {
            this.name = name;
            this.data = Arrays.copyOf(data, data.length);
            this.contentType = contentType;
            this.filename = filename;
            this.inputStream = null;
            this.value = null;
        }

        /**
         * Constructs a BodyPart
         * @param name see {@link BodyPart#getName()}
         * @param inputStream see {@link BodyPart#getInputStream()} ()}
         * @param contentType see {@link BodyPart#getContentType()}
         * @param filename see {@link BodyPart#getFilename()}
         */
        public BodyPart(String name, InputStream inputStream, String contentType, String filename) {
            this.name = name;
            this.inputStream = inputStream;
            this.contentType = contentType;
            this.filename = filename;
            this.data = null;
            this.value = null;
        }

        /**
         * Constructs a BodyPart
         * @param name see {@link BodyPart#getName()}
         * @param value see {@link BodyPart#getValue()} ()}
         */
        public BodyPart(String name, String value) {
            this.name = name;
            this.value = value;
            this.data = null;
            this.inputStream = null;
            this.contentType = null;
            this.filename = null;
        }

        /**
         * This is good practice to use and some API's may look for specific body parts by name
         * @return the optional name of this body part
         */
        public String getName() {
            return name;
        }

        /**
         * @return the raw data for this body part
         */
        public byte[] getData() {
            if (data == null) {
                return null;
            } else {
                return Arrays.copyOf(data, data.length);
            }
        }

        /**
         * @return the input stream for this body part
         */
        public InputStream getInputStream() {
            return inputStream;
        }

        /**
         * @return the value for this body part
         */
        public String getValue() {
            return value;
        }

        /**
         * @return the content type for this given body part
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * This can be useful to describe the original filename this body part was generated from
         * @return an optional file name for this body type
         */
        public String getFilename() {
            return filename;
        }
    }

}
