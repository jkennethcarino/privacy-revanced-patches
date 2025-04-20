package dev.jkcarino.revanced.util

import app.revanced.patcher.FingerprintBuilder
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.instructionsOrNull
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.util.proxy.ClassProxy
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.util.MethodUtil

/**
 * Applies a transformation to all methods of the [MutableClass] instance.
 *
 * This iterates through the [methods] of the [MutableClass], applies the
 * provided [transform] function to each method, and then updates the [methods] collection with the
 * transformed methods.
 */
fun MutableClass.transformMethods(transform: MutableMethod.() -> MutableMethod) {
    val transformedMethods = methods.map { it.transform() }
    methods.clear()
    methods.addAll(transformedMethods)
}

/**
 * Finds and returns the first matching method in the class hierarchy that matches the given [method].
 */
fun MutableClass.findMutableMethodOf(method: Method) = this.methods.first {
    MethodUtil.methodSignaturesMatch(it, method)
}

/**
 * Returns the method early.
 */
fun MutableMethod.returnEarly(bool: Boolean = false) {
    val const = if (bool) "0x1" else "0x0"

    val stringInstructions = when (returnType.first()) {
        'L' -> {
            """
                const/4 v0, $const
                return-object v0
            """
        }
        'I', 'Z' -> {
            """
                const/4 v0, $const
                return v0
            """
        }
        'V' -> "return-void"
        else -> throw Exception("This case should never happen.")
    }

    addInstructions(0, stringInstructions)
}

/**
 * Finds the index of the first wide literal instruction with the given value, or -1 if not found.
 */
fun Method.indexOfFirstLiteralInstruction(literal: Long) = implementation?.let {
    it.instructions.indexOfFirst { instruction ->
        (instruction as? WideLiteralInstruction)?.wideLiteral == literal
    }
} ?: -1

/**
 * Checks if the method contains a literal with the given value.
 */
fun Method.containsLiteralInstruction(literal: Long) =
    indexOfFirstLiteralInstruction(literal) >= 0

/**
 * Returns the [Reference] as [T] or null if the [Instruction] is not a
 * [ReferenceInstruction] or the [Reference] is not of type [T].
 *
 * See [ReferenceInstruction].
 */
inline fun <reified T : Reference> Instruction.getReference() =
    (this as? ReferenceInstruction)?.reference as? T

/**
 * Traverses the class hierarchy starting from the given root [targetClass].
 *
 * This first calls the provided [callback] with the [targetClass] as the argument. It then
 * recursively traverses the class hierarchy by finding the superclass of the [targetClass]
 * and calling this function with the superclass as the new [targetClass]. The recursion continues
 * until there are no more superclasses.
 *
 * The [callback] is called for every class in the hierarchy, allowing the caller to
 * perform some operation on each class as the hierarchy is traversed.
 */
fun BytecodePatchContext.traverseClassHierarchy(
    targetClass: MutableClass,
    callback: MutableClass.() -> Unit,
) {
    callback(targetClass)

    targetClass.superclass ?: return

    classBy { targetClass.superclass == it.type }?.mutableClass?.let {
        traverseClassHierarchy(it, callback)
    }
}

/**
 * Returns a proxy for the given [definingClass].
 */
fun BytecodePatchContext.proxy(definingClass: String): ClassProxy {
    return classBy { it.type == definingClass }
        ?: throw PatchException("Could not find $definingClass")
}

/**
 * Filters methods of this class based on the [predicate]. Only methods with
 * non-null instructions are considered.
 */
fun ClassDef.filterMethods(
    predicate: (ClassDef, Method) -> Boolean,
): List<Method> = buildList {
    val classDef = this@filterMethods
    methods.forEach { method ->
        method.instructionsOrNull ?: return@forEach
        if (predicate(classDef, method)) {
            add(method)
        }
    }
}

/**
 * Filters methods from all classes in the list based on the [predicate]. Only methods with
 * non-null instructions are considered.
 */
fun List<ClassDef>.filterMethods(
    predicate: (ClassDef, Method) -> Boolean,
): List<Method> = flatMap { it.filterMethods(predicate) }

/**
 * Sets the custom condition for this fingerprint to check for a literal value.
 */
// TODO: add a way for subclasses to also use their own custom fingerprint.
fun FingerprintBuilder.literal(literalSupplier: () -> Long) {
    custom { method, _ ->
        method.containsLiteralInstruction(literalSupplier())
    }
}
