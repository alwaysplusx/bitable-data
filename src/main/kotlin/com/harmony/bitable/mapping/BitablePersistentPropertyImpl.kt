package com.harmony.bitable.mapping

import com.harmony.bitable.BitfieldType
import org.springframework.data.mapping.Association
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder

internal class BitablePersistentPropertyImpl(
    property: Property,
    owner: BitablePersistentEntity<*>,
    simpleTypeHolder: SimpleTypeHolder,
) : AnnotationBasedPersistentProperty<BitablePersistentProperty>(property, owner, simpleTypeHolder),
    BitablePersistentProperty {

    private val field = owner.getRequiredField(property)

    override fun createAssociation() = Association(this, null)

    override fun getBitfieldId(): String? = field.fieldId

    override fun getBitfieldName(): String = field.fieldName

    override fun getBitfieldType(): BitfieldType = field.fieldType

    override fun isRecordIdProperty(): Boolean = field.isRecordIdField

}
