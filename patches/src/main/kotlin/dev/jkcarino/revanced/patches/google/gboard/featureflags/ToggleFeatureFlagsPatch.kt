package dev.jkcarino.revanced.patches.google.gboard.featureflags

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.stringsOption
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.bypassSignaturePatch
import java.util.logging.Logger

@Suppress("unused")
val toggleFeatureFlagsPatch = bytecodePatch(
    name = "Toggle feature flags",
    description = "Toggles Gboard feature flags to enable or disable experimental or hidden features.",
    use = false,
) {
    dependsOn(bypassSignaturePatch)

    compatibleWith("com.google.android.inputmethod.latin")

    val logger = Logger.getLogger(this::class.java.name)

    val featureFlags by stringsOption(
        key = "featureFlags",
        default = null,
        title = "Feature flags",
        description = "The Gboard feature flags to toggle, such as experimental or hidden features.",
        required = true
    ) { flags ->
        val flagsRegex = """^[A-Za-z0-9_-]+$""".toRegex()
        !flags.isNullOrEmpty() && flags.all { it.matches(flagsRegex) }
    }

    val enableFlags by booleanOption(
        key = "enableFlags",
        default = true,
        title = "Enable feature flags",
        description = "Enables or disables all specified feature flags."
    )

    execute {
        featureFlags!!.forEach { flag ->
            val fingerprint = featureFlagFingerprint(flag.trim())
            val state = if (enableFlags!!) "on" else "off"

            runCatching {
                fingerprint.method.apply {
                    val isEnabledIndex = fingerprint.patternMatch!!.endIndex
                    val isEnabledInstruction =
                        getInstruction<OneRegisterInstruction>(isEnabledIndex)
                    val isEnabledRegister = isEnabledInstruction.registerA
                    val enabled = if (enableFlags!!) "0x1" else "0x0"

                    replaceInstructions(
                        index = isEnabledIndex,
                        smaliInstructions = "const/4 v$isEnabledRegister, $enabled"
                    )
                }
            }.onSuccess {
                logger.info("[Found] \"$flag\" toggled $state.")
            }.onFailure {
                logger.info("[Skipped] \"$flag\" was not found. No changes applied.")
            }
        }
    }
}
