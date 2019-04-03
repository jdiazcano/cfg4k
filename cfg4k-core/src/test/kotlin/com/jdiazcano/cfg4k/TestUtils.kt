package com.jdiazcano.cfg4k

internal fun setEnv(key: String, value: String) {
    try {
        val env = System.getenv()
        val cl = env.javaClass
        val field = cl.getDeclaredField("m")
        field.isAccessible = true
        val writableEnv = field.get(env) as MutableMap<String, String>
        writableEnv.put(key, value)
    } catch (e: Exception) {
        throw IllegalStateException("Failed to set environment variable", e)
    }

}