package dev.jkcarino.revanced.util

import app.revanced.patcher.FingerprintBuilder
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction

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
 * Sets the custom condition for this fingerprint to check for a literal value.
 */
// TODO: add a way for subclasses to also use their own custom fingerprint.
fun FingerprintBuilder.literal(literalSupplier: () -> Long) {
    custom { method, _ ->
        method.containsLiteralInstruction(literalSupplier())
    }
}
