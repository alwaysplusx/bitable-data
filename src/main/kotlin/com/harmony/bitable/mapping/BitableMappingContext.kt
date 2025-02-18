package com.harmony.bitable.mapping

import com.harmony.bitable.BitableSource
import org.springframework.data.mapping.context.AbstractMappingContext
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.util.TypeInformation

class BitableMappingContext(private val bitableSource: BitableSource) :
    AbstractMappingContext<BitablePersistentEntity<*>, BitablePersistentProperty>() {

    init {
        this.setSimpleTypeHolder(object : SimpleTypeHolder(emptySet(), true) {
            override fun isSimpleType(type: Class<*>): Boolean {
                if (type.name.startsWith("com.lark.oapi")) {
                    return true
                }
                return super.isSimpleType(type)
            }
        })
    }

    override fun <T> createPersistentEntity(typeInformation: TypeInformation<T>): BitablePersistentEntity<*> {
        val bitity = bitableSource.getBitity(typeInformation.type)
        val bitable = bitableSource.getBitable(bitity.name)
        return BasicBitablePersistentEntity(typeInformation, bitable, bitity)
    }

    override fun createPersistentProperty(
        property: Property,
        owner: BitablePersistentEntity<*>,
        simpleTypeHolder: SimpleTypeHolder
    ): BitablePersistentProperty = BasicBitablePersistentProperty(property, owner, simpleTypeHolder)


}
