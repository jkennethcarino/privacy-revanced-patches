package dev.jkcarino.revanced.patches.reddit.ad

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef

internal val interceptFingerprint = fingerprint {
    returns("Lokhttp3/Response;")
    parameters("Lokhttp3/Interceptor\$Chain;")
    opcodes(
        // responseBody.source()
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,

        // source.request(Long.MAX_VALUE)
        Opcode.CONST_WIDE,
        Opcode.INVOKE_INTERFACE,

        // source.getBuffer()
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        // .clone()
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,

        // contentType.charset(StandardCharsets.UTF_8)
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,

        // buffer.readString(charset)
        Opcode.INVOKE_VIRTUAL,
    )
}

internal val okHttpConstructorFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    parameters("Lokhttp3/OkHttpClient\$Builder;")
    opcodes(
        Opcode.CONST_STRING,
        Opcode.INVOKE_STATIC,
        null,
        null,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IPUT_OBJECT,
        null,
        Opcode.MOVE_RESULT_OBJECT,
    )
    custom { _, classDef ->
        classDef.type == "Lokhttp3/OkHttpClient;"
    }
}

internal val realBufferedSourceCommonIndexOfFingerprint = fingerprint {
    returns("J")
    parameters("B", "J", "J")
    strings(
        "fromIndex=0 toIndex=",
        "closed",
    )
}

internal val bufferCommonReadAndWriteUnsafeFingerprint = fingerprint {
    returns("L")
    parameters("L")
    strings("already attached to a buffer")
}

internal val bufferReadStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters("Ljava/nio/charset/Charset;")
    strings("charset")
}

internal val bufferCloneFingerprint = fingerprint {
    accessFlags(
        AccessFlags.PUBLIC,
        AccessFlags.FINAL,
        AccessFlags.BRIDGE,
        AccessFlags.SYNTHETIC
    )
    custom { method, _ ->
        method.name == "clone"
    }
}

internal val bufferedSourceGetBufferFingerprint = { classDef: ClassDef ->
    fingerprint {
        returns(classDef.type)
        parameters()
        opcodes(Opcode.RETURN_OBJECT)
    }
}
