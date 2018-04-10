package uk.co.autotrader.traverson.http;

import java.nio.charset.Charset;

public class TextBody implements Body<String> {
    private final String data;
    private final String contentType;
    private final Charset charset;

    public TextBody(String data, String contentType) {
        this(data, contentType, null);
    }

    public TextBody(String data, String contentType, Charset charset) {
        this.data = data;
        this.contentType = contentType;
        this.charset = charset;
    }

    @Override
    public String getContent() {
        return data;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public Charset getCharset() {
        return charset;
    }
}
