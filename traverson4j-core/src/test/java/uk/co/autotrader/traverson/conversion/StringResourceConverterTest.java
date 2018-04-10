package uk.co.autotrader.traverson.conversion;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringResourceConverterTest {

    private StringResourceConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new StringResourceConverter();
    }

    @Test
    public void canConvert_GivenStringClass_ReturnsTrue() throws Exception {
        assertThat(converter.getDestinationType()).isEqualTo(String.class);
    }

    @Test
    public void convert_ReturnsTheInputString() throws Exception {
        String resourceAsString = "My Resource";

        assertThat(converter.convert(resourceAsString, String.class)).isEqualTo(resourceAsString);
    }
}