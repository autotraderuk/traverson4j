package uk.co.autotrader.traverson.conversion;

import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Singleton service that converts String representations of resources
 * to available types
 */
public class ResourceConversionService {

    private final Map<Class<?>, ResourceConverter<?>> convertersByClass;
    private static ResourceConversionService instance;

    ResourceConversionService(Map<Class<?>, ResourceConverter<?>> convertersByClass) {
        this.convertersByClass = convertersByClass;
    }

    /**
     * @return singleton instance
     */
    public static ResourceConversionService getInstance() {
        if (instance == null) {
            ResourceConversionService conversionService = new ResourceConversionService(new LinkedHashMap<>());
            conversionService.addConverter(new FastJsonResourceConverter());
            conversionService.addConverter(new StringResourceConverter());
            conversionService.addConverter(new ByteArrayConverter());
            conversionService.addConverter(new InputStreamConverter());
            for (ResourceConverter resourceConverter : ServiceLoader.load(ResourceConverter.class)) {
                conversionService.addConverter(resourceConverter);
            }
            instance = conversionService;
        }

        return instance;
    }

    /**
     * Register programmatically a new Resource converter
     * @param resourceConverter item to register for future use
     */
    public void addConverter(ResourceConverter<?> resourceConverter) {
        convertersByClass.put(resourceConverter.getDestinationType(), resourceConverter);
    }

    /**
     * <p>Convert a resource to the given returnType</p>
     * <p>It will attempt to find the resourceConverter that satisfies the
     * returnType or one of its parent classes.</p>
     * @param <T> return type
     * @param resource the full returned resource
     * @param returnType class of the return type
     * @throws ConversionException if a suitable converter cannot be found or if the converter fails
     * @return instance of the returnType
     */
    @SuppressWarnings("unchecked")
    public <T> T convert(InputStream resource, Class<T> returnType) {
        Class<? super T> classToConvert = returnType;
        while (classToConvert != null) {
            if (convertersByClass.containsKey(classToConvert)) {
                //TODO: Nicer cast
                return ((ResourceConverter<T>) convertersByClass.get(classToConvert)).convert(resource, returnType);
            }

            classToConvert = classToConvert.getSuperclass();
        }
        throw new ConversionException("Unsupported return type of " + returnType.getCanonicalName());
    }

    Map<Class<?>, ResourceConverter<?>> getConvertersByClass() {
        return convertersByClass;
    }
}
