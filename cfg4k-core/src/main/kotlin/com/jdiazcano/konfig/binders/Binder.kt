package com.jdiazcano.konfig.binders

import com.jdiazcano.konfig.providers.ConfigProvider
import kotlin.reflect.KClass


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
    fun <T: Any> bind(provider: ConfigProvider, prefix: String, type: KClass<T>): T
}