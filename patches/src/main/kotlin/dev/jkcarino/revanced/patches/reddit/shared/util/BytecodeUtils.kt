package dev.jkcarino.revanced.patches.reddit.shared.util

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.instructions
import app.revanced.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import dev.jkcarino.revanced.util.getReference

fun BytecodePatchContext.updateClassField(
    classDef: ClassDef,
    fieldReference: FieldReference,
    value: Any?,
) {
    val boolValue =
        when (value) {
            null -> "0x0"
            is Boolean -> if (value) "0x1" else "0x0"
            else -> throw IllegalArgumentException("Unsupported value type: ${value.javaClass}")
        }

    val constructor = proxy(classDef)
        .mutableClass
        .methods
        .first { method -> method.name == "<init>" }

    val fieldIndex = constructor
        .instructions
        .indexOfFirst { instruction ->
            val currentFieldReference = instruction
                .getReference<FieldReference>()
                ?: return@indexOfFirst false

            currentFieldReference.definingClass == fieldReference.definingClass
                && currentFieldReference.name == fieldReference.name
                && currentFieldReference.type == fieldReference.type
        }

    val paramRegister = constructor
        .getInstruction<TwoRegisterInstruction>(fieldIndex)
        .registerA

    constructor.addInstruction(
        index = fieldIndex,
        smaliInstructions = "const/4 v$paramRegister, $boolValue"
    )
}
