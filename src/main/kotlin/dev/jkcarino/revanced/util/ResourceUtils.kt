package dev.jkcarino.revanced.util

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * Returns a sequence for all child nodes.
 */
fun NodeList.asSequence(): Sequence<Node> =
    (0 until this.length).asSequence().map { this.item(it) }

/**
 * Filters the child nodes to return a sequence of only those that are [Element]s.
 */
fun NodeList.filterToElements(): Sequence<Element> =
    asSequence().mapNotNull { it as? Element }

/**
 * Returns the first element in the [Document] with the specified tag name.
 */
fun Document.firstElementByTagName(tagName: String): Element =
    getElementsByTagName(tagName).item(0) as Element

/**
 * Creates a new element with the specified tag name and attributes.
 */
inline fun Document.createElement(
    tagName: String,
    init: Element.() -> Unit
): Element {
    val element = createElement(tagName)
    element.init()
    return element
}
