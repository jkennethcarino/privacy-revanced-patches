package dev.jkcarino.revanced.patches.all.firebase.analytics

import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.shared.resource.BaseResourceElementRemovalPatch
import dev.jkcarino.revanced.patches.shared.resource.PermissionElement
import dev.jkcarino.revanced.patches.shared.resource.PropertyElement

@Patch(description = "Removes AdServices config and permissions.")
object RemoveAdServicesPatch : BaseResourceElementRemovalPatch(
    PermissionElement(
        "android.permission.ACCESS_ADSERVICES_ATTRIBUTION",
        "android.permission.ACCESS_ADSERVICES_AD_ID",
    ),
    PropertyElement(
        "android.adservices.AD_SERVICES_CONFIG",
    ),
)
