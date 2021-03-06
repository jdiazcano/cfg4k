package com.jdiazcano.cfg4k.binders

import com.jdiazcano.cfg4k.providers.ConfigProvider

/**
 * Interface that defines the method for binding an interface for a configuration.
 */
interface Binder {

    /**
     * Binds an interface
     *
     * This method will return an implementation of the interface with the given methods for configuration.
     *
     * @param prefix The prefix of the configuration, if this is not empty, configs starting with the prefix will be used
     * @param type The interface that will be implemented and it will be returned
     */
    fun <T : Any> bind(configProvider: ConfigProvider, prefix: String, type: Class<T>): T
}