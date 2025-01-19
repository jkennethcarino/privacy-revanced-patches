package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.rawResourcePatch
import org.bouncycastle.cms.CMSSignedData
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.security.Security
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

        val originalBcProvider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
        val relocatedBcProvider = BouncyCastleProvider()

        if (originalBcProvider != null) {
            Security.removeProvider(originalBcProvider.name)
        }
        Security.addProvider(relocatedBcProvider)

        val pkcs7 = CMSSignedData(certificateFile.readBytes())
        val certificates = pkcs7.certificates.getMatches(null)

        ByteArrayOutputStream().use { baos ->
            DataOutputStream(baos).use { dos ->
                dos.write(certificates.size)
                certificates.forEach { certificate ->
                    val data = certificate.encoded
                    dos.writeInt(data.size)
                    dos.write(data)
                }

                signature = Base64.getEncoder()
                    .encodeToString(baos.toByteArray())
            }
        }

        // We need to remove our relocated BC provider to avoid any potential issues
        Security.removeProvider(relocatedBcProvider.name)

        if (originalBcProvider != null) {
            // and make sure to revert back to the original if there's any
            Security.addProvider(originalBcProvider)
        }
    }
}
