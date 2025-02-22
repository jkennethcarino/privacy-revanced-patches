package dev.jkcarino.revanced.patches.all.contentblocker.ads

import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.admob.applyGoogleAdMobPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.admob.disableGoogleAdMobOption
import java.util.logging.Logger

@Suppress("unused")
val disableAdsPatch = bytecodePatch(
    name = "Disable mobile ads",
    description = "Disables banner, interstitial, and other ad formats."
) {
    val logger = Logger.getLogger(this::class.java.name)

    execute {
        val options = mapOf(
            disableGoogleAdMobOption to ::applyGoogleAdMobPatch,
        )

        options.forEach { (option, patch) ->
            val isEnabled by option
            if (!isEnabled!!) {
                return@forEach
            }

            val message = try {
                patch()

                "[Found] ${option.title} disabled."
            } catch (_: PatchException) {
                "[Skipped] ${option.title} was not found."
            }

            logger.info(message)
        }
    }
}
