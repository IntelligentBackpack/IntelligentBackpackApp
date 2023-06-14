package com.intelligentbackpack.app.sensor

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Barcode analyser
 *
 * @param onBarcodeFound Callback for when a barcode is found
 * @param options Barcode scanner options
 */
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class BarcodeAnalyser(
    val onBarcodeFound: (String) -> Unit,
    val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_EAN_13)
        .build(),
) : ImageAnalysis.Analyzer {
    override fun analyze(imageProxy: ImageProxy) {
        val scanner = BarcodeScanning.getClient(options)
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.size > 0) {
                        onBarcodeFound(barcodes[0].rawValue ?: "")
                    }
                }
                .addOnFailureListener {
                }
        }
        imageProxy.close()
    }
}
