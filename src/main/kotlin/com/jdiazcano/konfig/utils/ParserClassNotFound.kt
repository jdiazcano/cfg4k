package com.jdiazcano.konfig.utils

/**
 * This exception will be thrown when a parser is not defined for a specific class. If you see this error you need to
 * call the methods "addParser" "addClassedParser" of the Provider
 */
class ParserClassNotFound(message: String) : Exception(message)