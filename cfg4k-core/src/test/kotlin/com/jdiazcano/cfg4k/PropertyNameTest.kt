package com.jdiazcano.cfg4k

import com.jdiazcano.cfg4k.binders.getPropertyName
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class PropertyNameTest: Spek({
    it("should strip the get is or has from the methods that needs") {
        getPropertyName("getTest").should.be.equal("test")
        getPropertyName("isomorphic").should.be.equal("isomorphic")
        getPropertyName("isOmorphic").should.be.equal("omorphic")
        getPropertyName("hash").should.be.equal("hash")
        getPropertyName("hasTests").should.be.equal("tests")
        getPropertyName("getИмя").should.be.equal("имя")
        getPropertyName("getимя").should.be.equal("getимя")
    }
})