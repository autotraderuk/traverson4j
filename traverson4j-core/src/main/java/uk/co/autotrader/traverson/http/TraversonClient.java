package uk.co.autotrader.traverson.http;

public interface TraversonClient {

    //TODO: comment how it will throw Http Exception
    <T> Response<T> execute(Request request, Class<T> returnType);

}
