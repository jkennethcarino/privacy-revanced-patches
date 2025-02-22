package dev.jkcarino.revanced.patches.all.contentblocker.ads

import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.admob.applyGoogleAdMobPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.admob.disableGoogleAdMobOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.pangle.applyPanglePatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.pangle.disablePangleOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.topon.applyTopOnPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.topon.disableTopOnOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.vungle.applyVunglePatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.vungle.disableVungleOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex.applyYandexPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex.disableYandexOption
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
            disablePangleOption to ::applyPanglePatch,
            disableTopOnOption to ::applyTopOnPatch,
            disableVungleOption to ::applyVunglePatch,
            disableYandexOption to ::applyYandexPatch,
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
