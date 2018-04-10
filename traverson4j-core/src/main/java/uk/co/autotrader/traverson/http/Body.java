package uk.co.autotrader.traverson.http;

public interface Body<T> {
    T getContent();
    String getContentType();
}
