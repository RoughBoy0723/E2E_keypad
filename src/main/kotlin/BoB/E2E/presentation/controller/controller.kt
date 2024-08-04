package BoB.E2E.presentation.controller

import BoB.E2E.domain.service.ImageMergeService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.UUID
import java.time.Instant
import javax.imageio.ImageIO
import java.io.File

@RestController
@RequestMapping("/api/images")
class ImageMergeController(private val imageMergeService: ImageMergeService) {

    @GetMapping("/merge-keypad")
    fun mergeKeypadImages(): ResponseEntity<ByteArray> {
        val resourcePath = "src/main/resources/keypad"
        val imageFiles = (0..9).map { File("$resourcePath/_${it}.png") } + File("$resourcePath/_blank.png")

        val mergedImage = imageMergeService.mergeImages(imageFiles)

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(mergedImage, "png", outputStream)
        val imageBytes = outputStream.toByteArray()

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_TYPE, "image/png")

        return ResponseEntity.ok().headers(headers).body(imageBytes)
    }

    @GetMapping("/merge-keypad-random")
    fun mergeRandomKeypadImages(): ResponseEntity<Map<String, Any>> {
        val resourcePath = "src/main/resources/keypad"

        val (mergedImage, hashValues) = imageMergeService.mergeRandomKeypadImages(resourcePath)

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(mergedImage, "png", outputStream)
        val imageBytes = outputStream.toByteArray()
        val encodedImage = Base64.getEncoder().encodeToString(imageBytes)

        val responseBody = mapOf(
            "image" to encodedImage,
            "hashes" to hashValues,
            "timestamp" to Instant.now().toString(),
            "uuid" to UUID.randomUUID().toString()
        )

        return ResponseEntity.ok().body(responseBody)
    }
}
