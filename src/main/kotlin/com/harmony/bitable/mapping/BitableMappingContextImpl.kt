package com.harmony.bitable.mapping

import com.harmony.bitable.BititySource
import com.harmony.bitable.BitableSource
import org.springframework.data.mapping.context.AbstractMappingContext
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.util.TypeInformation

internal class BitableMappingContextImpl(
    private val bitableSource: BitableSource,
    private val bititySource: BititySource,
) : AbstractMappingContext<BitablePersistentEntity<*>, BitablePersistentProperty>(),
    BitableMappingContext {

    init {
        this.setSimpleTypeHolder(BITABLE_SIMPLE_TYPE_HOLDER)
    }

    companion object {
        private val BITABLE_SIMPLE_TYPE_HOLDER = BitableSimpleTypeHolder()
    }

    override fun <T : Any> createPersistentEntity(typeInformation: TypeInformation<T>): BitablePersistentEntity<*> {
        val bitity = bititySource.getBitity(typeInformation.type)
        val bitable = bitableSource.getBitable(bitity.getName())
        return BitablePersistentEntityImpl(typeInformation, bitable, bitity)
    }

    override fun createPersistentProperty(
        property: Property,
        owner: BitablePersistentEntity<*>,
        simpleTypeHolder: SimpleTypeHolder,
    ) = BitablePersistentPropertyImpl(property, owner, simpleTypeHolder)

    private class BitableSimpleTypeHolder : SimpleTypeHolder(emptySet(), true) {

        override fun isSimpleType(type: Class<*>): Boolean {
            if (type.name.startsWith("com.lark.oapi")) {
                return true
            }
            return super.isSimpleType(type)
        }

    }

}
