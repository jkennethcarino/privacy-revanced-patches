package dev.jkcarino.revanced.util

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction

/**
 * The [PatchException] that is thrown when failing to resolve a [MethodFingerprint].
 */
val MethodFingerprint.exception
    get() = PatchException("Failed to resolve ${this.javaClass.simpleName}")

/**
 * Finds the index of the first wide literal instruction with the given value, or -1 if not found.
 */
fun Method.indexOfFirstWideLiteralInstructionValue(literal: Long) = implementation?.let {
    it.instructions.indexOfFirst { instruction ->
        (instruction as? WideLiteralInstruction)?.wideLiteral == literal
    }
} ?: -1

/**
 * Checks if the method contains a literal with the given value.
 */
fun Method.containsWideLiteralInstructionValue(literal: Long) =
    indexOfFirstWideLiteralInstructionValue(literal) >= 0

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
fun BytecodeContext.traverseClassHierarchy(
    targetClass: MutableClass,
    callback: MutableClass.() -> Unit
) {
    callback(targetClass)
    this.findClass(targetClass.superclass ?: return)?.mutableClass?.let {
        traverseClassHierarchy(it, callback)
    }
}
