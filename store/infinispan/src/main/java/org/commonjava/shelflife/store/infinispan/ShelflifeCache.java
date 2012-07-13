package org.commonjava.shelflife.store.infinispan;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.infinispan.Cache;

/**
 * Annotation used to produce and inject Infinispan {@link Cache} instances for use with Shelflife's
 * {@link InfinispanExpirationManager}.
 */
@Qualifier
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface ShelflifeCache
{

}
