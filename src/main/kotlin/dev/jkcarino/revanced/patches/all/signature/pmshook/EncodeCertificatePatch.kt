package dev.jkcarino.revanced.patches.all.signature.pmshook

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.RawResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import org.bouncycastle.cms.CMSSignedData
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.security.Security
import java.util.Base64

@Patch(description = "Extracts and encodes the digital certificate to Base64.")
object EncodeCertificatePatch : RawResourcePatch() {
    internal lateinit var signature: String
        private set

    override fun execute(context: ResourceContext) {
        fun File.isCertificate(): Boolean {
            return isFile && (extension == "RSA" || extension == "DSA")
        }

        val certificateFile = context["META-INF", true].listFiles()
            ?.firstOrNull(File::isCertificate)
            ?: throw PatchException("META-INF/*.RSA or *.DSA file not found.")

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())

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
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
    }
}
