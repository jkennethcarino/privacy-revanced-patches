package dev.jkcarino.revanced.patches.all.firebase.analytics

import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.shared.resource.BaseResourceElementRemovalPatch
import dev.jkcarino.revanced.patches.shared.resource.PermissionElement

@Patch(description = "Removes the Advertising ID permission.")
object RemoveAdvertisingIdPatch : BaseResourceElementRemovalPatch(
    PermissionElement(
        "com.google.android.gms.permission.AD_ID",
    )
)
