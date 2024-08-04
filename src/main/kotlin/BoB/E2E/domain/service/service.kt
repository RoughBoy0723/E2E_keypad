package BoB.E2E.domain.service

import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.security.MessageDigest
import java.util.UUID

@Service
class ImageMergeService {
    fun mergeImages(imageFiles: List<File>): BufferedImage {
        if (imageFiles.isEmpty()) {
            throw IllegalArgumentException("No image files provided")
        }

        val firstImage = ImageIO.read(imageFiles[0])
        val totalWidth = firstImage.width * imageFiles.size
        val height = firstImage.height

        val mergedImage = BufferedImage(totalWidth, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = mergedImage.graphics

        for ((index, imageFile) in imageFiles.withIndex()) {
            val image = ImageIO.read(imageFile)
            graphics.drawImage(image, index * firstImage.width, 0, null)
        }

        graphics.dispose()
        return mergedImage
    }

    fun mergeRandomKeypadImages(resourcePath: String): Pair<BufferedImage, List<String?>> {
        val imageFiles = (0..9).map { File("$resourcePath/_${it}.png") to it.toString() } +
                List(2) { File("$resourcePath/_blank.png") to null }

        val shuffledImages = imageFiles.shuffled()

        val imagePaths = shuffledImages.map { it.first }
        val hashValues = shuffledImages.map { it.second?.let { value -> calculateSha256(value) } }

        val mergedImage = mergeImages(imagePaths)
        return mergedImage to hashValues
    }

    private fun calculateSha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(value.toByteArray())
        return hashBytes.joinToString("") { byte -> "%02x".format(byte) }
    }
}
