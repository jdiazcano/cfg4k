package com.jdiazcano.cfg4k.binders

import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe

class BindingInvocationHandlerKtTest : StringSpec({

    "should strip the get is or has from the methods that needs" {
        getPropertyName("getTest") shouldBe "test"
        getPropertyName("isomorphic") shouldBe "isomorphic"
        getPropertyName("isOmorphic") shouldBe "omorphic"
        getPropertyName("hash") shouldBe "hash"
        getPropertyName("hasTests") shouldBe "tests"
        getPropertyName("getИмя") shouldBe "имя"
        getPropertyName("getимя") shouldBe "getимя"
    }

})