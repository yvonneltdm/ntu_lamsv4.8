package org.lamsfoundation.lams.tool.forum.core;

import org.springframework.context.ApplicationContext;

/**
 * GenericObjectFactory
 */
public interface GenericObjectFactory {


    /**
     * lookup binding by name
     */
    public Object lookup(String binding) throws FactoryException;

    /**
     * lookup binding by class type
     */
    public Object lookup(Class type) throws FactoryException;

}
