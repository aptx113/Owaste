package com.epoch.owaste.qrcode


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log.i
import android.util.SparseArray
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.util.size
import com.epoch.owaste.data.OwasteRepository
import com.epoch.owaste.databinding.FragmentQrcodeScannerBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.coroutines.Runnable
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class QRCodeScannerFragment : Fragment() {

    val TAG = "Eltin_" + this::class.java.simpleName
    private lateinit var binding: FragmentQrcodeScannerBinding
    private lateinit var qrCodeScanner: SurfaceView
    private lateinit var scanResult: TextView
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQrcodeScannerBinding.inflate(inflater, container, false)

        binding.let {

            qrCodeScanner = it.svQrCodeScanner
            scanResult = it.txtScanResult
        }

        barcodeDetector = BarcodeDetector.Builder(this.requireContext())
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val windowHeight = metrics.heightPixels
        val windowWidth = metrics.widthPixels

        i(TAG, "surfaceview = $windowWidth, ${binding.svQrCodeScanner.layoutParams.height}")
        cameraSource = CameraSource.Builder(this.requireContext(), barcodeDetector)
//                .setRequestedPreviewSize(windowWidth, binding.svQrCodeScanner.layoutParams.height)
                .setAutoFocusEnabled(true)
                .build()

        qrCodeScanner.holder.addCallback(object : SurfaceHolder.Callback {

            override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
                if (ActivityCompat.checkSelfPermission(
                        this@QRCodeScannerFragment.requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {

                    try {
                        runWithPermissions(Manifest.permission.CAMERA) {

                            cameraSource.start(surfaceHolder)
                            i(TAG, "好啦准你開相機")
                            i(TAG, "window width = $windowWidth , " +
                            "window height = $windowHeight")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {

                        cameraSource.start(surfaceHolder)
                        i(TAG, "VIP pass 相機直接開起來")
                }
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder?) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {

            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {

                val qrcode: SparseArray<Barcode>? = detections?.detectedItems

                if (qrcode?.size != 0) {

                    scanResult.post(Runnable {

                            val result: String? = qrcode?.valueAt(0)?.displayValue
                            // get level and card Id of the restaurants from QR code
                            OwasteRepository._currentQRCodeCardId.value =
                                result?.let {
                                    it.substring(it.indexOf("a") + 1, it.indexOf("b"))
                                }
                            OwasteRepository._currentQRCodeLevel.value =
                                result?.let {
                                    it.substring(it.indexOf("b") + 1, it.indexOf("c"))
                                }
                            OwasteRepository._currentQRCodeRestaurantName.value =
                                result?.let {
                                    it.substring(it.indexOf("c") + 1)
                                }

                            i(TAG, "OwasteRepository._currentLevelsOfAllCards.value = ${OwasteRepository._currentQRCodeLevel.value}")
                            i(TAG, "OwasteRepository._currentQRCodeCardId.value = ${OwasteRepository._currentQRCodeCardId.value}")
                            i(TAG, "OwasteRepository._currentQRCodeRestaurantName.value = ${OwasteRepository._currentQRCodeRestaurantName.value}")

                            barcodeDetector.release()
                            OwasteRepository.onQRCodeScannedUpdateCard()
                            OwasteRepository.onQRCodeScannedUpdateExp()
                        }
                    )
                }
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }
}
