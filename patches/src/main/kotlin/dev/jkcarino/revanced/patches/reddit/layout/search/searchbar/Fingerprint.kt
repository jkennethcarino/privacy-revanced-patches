package dev.jkcarino.revanced.patches.reddit.layout.search.searchbar

import app.revanced.patcher.fingerprint

internal val isEnabledFingerprint = fingerprint {
    returns("Z")
    parameters()
    custom { method, classDef ->
        method.name == "isEnabled"
            && classDef.type.endsWith("/feed/revamp/HomeRevampVariant;")
    }
}
