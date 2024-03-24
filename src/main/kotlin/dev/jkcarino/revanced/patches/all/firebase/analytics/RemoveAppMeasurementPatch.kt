package dev.jkcarino.revanced.patches.all.firebase.analytics

import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.shared.resource.BaseResourceElementRemovalPatch
import dev.jkcarino.revanced.patches.shared.resource.ReceiverElement
import dev.jkcarino.revanced.patches.shared.resource.ServiceElement

@Patch(description = "Removes App Measurement's broadcast receivers and services.")
object RemoveAppMeasurementPatch : BaseResourceElementRemovalPatch(
    ReceiverElement("""com\.google\.android\.gms\.measurement\..+Receiver$"""),
    ServiceElement("""com\.google\.android\.gms\.measurement\..+Service$"""),
)
