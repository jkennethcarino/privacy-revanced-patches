package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.rawResourcePatch
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64

internal lateinit var signature: String
    private set

val encodeCertificatePatch = rawResourcePatch(
    description = "Extracts and encodes the digital certificate to Base64."
) {
    execute {
        fun File.isCertificate(): Boolean {
            return isFile && (extension == "RSA" || extension == "DSA")
        }

        val certificateFile = get("META-INF").listFiles()
            ?.firstOrNull(File::isCertificate)
            ?: throw PatchException("META-INF/*.RSA or *.DSA file not found.")

        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFile
            .inputStream()
            .use { inputStream ->
                certificateFactory
                    .generateCertificates(inputStream)
                    .filterIsInstance<X509Certificate>()
            }

        signature = ByteArrayOutputStream().use { baos ->
            DataOutputStream(baos).use { dos ->
                dos.write(certificates.size)

                certificates.forEach { certificate ->
                    val data = certificate.encoded
                    dos.writeInt(data.size)
                    dos.write(data)
                }

                Base64
                    .getEncoder()
                    .encodeToString(baos.toByteArray())
            }
        }
    }
}
