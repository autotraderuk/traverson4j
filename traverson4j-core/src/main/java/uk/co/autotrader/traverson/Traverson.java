package uk.co.autotrader.traverson;

import uk.co.autotrader.traverson.http.TraversonClient;

/**
 * Suitable as an Injectable singleton
 */
public class Traverson {
    private TraversonClient traversonClient;

    /**
     * Creates the traverson client
     * @param traversonClient - Traverson client with your custom settings
     */
    public Traverson(TraversonClient traversonClient) {
        this.traversonClient = traversonClient;
    }

    /**
     * Start the navigation here
     * @param startingUrl - Starting Url for the project
     * @return a new stateful TraversonBuilder
     */
    public TraversonBuilder from(String startingUrl) {
        return new TraversonBuilder(traversonClient).from(startingUrl);
    }
}
