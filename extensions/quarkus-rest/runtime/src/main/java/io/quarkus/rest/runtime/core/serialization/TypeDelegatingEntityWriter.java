package io.quarkus.rest.runtime.core.serialization;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

import io.quarkus.rest.runtime.core.QuarkusRestRequestContext;

/**
 * An entity writer that will delegate based on the actual type of the
 * returned entity.
 */
public class TypeDelegatingEntityWriter implements EntityWriter {

    private final Map<Class<?>, EntityWriter> typeMap;

    public TypeDelegatingEntityWriter(Map<Class<?>, EntityWriter> typeMap) {
        this.typeMap = typeMap;
    }

    @Override
    public void write(QuarkusRestRequestContext context, Object entity) throws IOException {
        Class<?> c = entity.getClass();
        while (c != null) {
            EntityWriter delegate = typeMap.get(c);
            if (delegate != null) {
                delegate.write(context, entity);
                return;
            }
            c = c.getSuperclass();
        }
        throw new InternalServerErrorException("Could not find MessageBodyWriter for " + entity.getClass(),
                Response.serverError().build());
    }
}