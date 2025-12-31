package dev.jkcarino.revanced.patches.reddit.misc.outboundlink

import app.revanced.patcher.fingerprint

internal val accountPreferencesToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings("AccountPreferences(over18=")
}

internal val getAllowClickTrackingFingerprint = fingerprint {
    returns("Z")
    custom { method, _ ->
        method.name == "getAllowClickTracking"
    }
}

internal val accountToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings("Account(id=")
}

internal val getOutboundClickTrackingFingerprint = fingerprint {
    returns("Z")
    custom { method, _ ->
        method.name == "getOutboundClickTracking"
    }
}

internal val getOutboundLinkFingerprint = fingerprint {
    returns("L")
    parameters()
    custom { method, _ ->
        method.name == "getOutboundLink"
    }
}
