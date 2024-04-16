package dev.jkcarino.revanced.patches.shared.resource

import dev.jkcarino.revanced.util.filterElements
import dev.jkcarino.revanced.util.get
import dev.jkcarino.revanced.util.removeElements
import org.w3c.dom.Document

interface ResourceRemover {
    fun process(document: Document)
}

/**
 * This removes Broadcast Receivers matching a specified [receiverRegex] from the
 * <application> element.
 */
class ReceiverElement(receiverRegex: String) : ResourceRemover {
    private val regex = receiverRegex.toRegex()

    override fun process(document: Document) {
        val application = document["application"]

        application.getElementsByTagName("receiver")
            .filterElements { it["android:name"].matches(regex) }
            .let(application::removeElements)
    }
}

/**
 * This removes Services matching a specified [serviceRegex] from the
 * <application> element.
 */
class ServiceElement(serviceRegex: String) : ResourceRemover {
    private val regex = serviceRegex.toRegex()

    override fun process(document: Document) {
        val application = document["application"]

        application.getElementsByTagName("service")
            .filterElements { it["android:name"].matches(regex) }
            .let(application::removeElements)
    }
}

/**
 * This removes specified [permissions] from the <manifest> element.
 */
class PermissionElement(vararg permissions: String) : ResourceRemover {
    private val usesPermissions = permissions.toSet()

    override fun process(document: Document) {
        val manifest = document.documentElement

        document.getElementsByTagName("uses-permission")
            .filterElements { it["android:name"] in usesPermissions }
            .let(manifest::removeElements)
    }
}

/**
 * This removes specified [props] from the <application> element.
 */
class PropertyElement(vararg props: String) : ResourceRemover {
    private val properties = props.toSet()

    override fun process(document: Document) {
        val application = document["application"]

        application.getElementsByTagName("property")
            .filterElements { it["android:name"] in properties }
            .let(application::removeElements)
    }
}
