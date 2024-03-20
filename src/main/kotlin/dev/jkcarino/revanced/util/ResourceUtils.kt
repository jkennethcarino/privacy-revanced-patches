package dev.jkcarino.revanced.util

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

/**
 * Returns the first element in the [Document] with the specified tag name.
 */
fun Document.firstElementByTagName(tagName: String): Element =
    getElementsByTagName(tagName).item(0) as Element

/**
 * Filters the child nodes to return a sequence of only those that are [Element]s.
 */
fun NodeList.asElementSequence(): Sequence<Element> =
    (0 until this.length)
        .asSequence()
        .mapNotNull { this.item(it) as? Element }

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
