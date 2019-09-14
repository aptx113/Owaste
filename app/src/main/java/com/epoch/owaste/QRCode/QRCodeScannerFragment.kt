package com.epoch.owaste.QRCode


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

    lateinit var binding: FragmentQrcodeScannerBinding
    lateinit var qrCodeScanner: SurfaceView
    lateinit var scanResult: TextView
    lateinit var cameraSource: CameraSource
    lateinit var barcodeDetector: BarcodeDetector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        getCameraPermission()

        binding = FragmentQrcodeScannerBinding.inflate(inflater, container, false)

        qrCodeScanner = binding.svQrCodeScanner
        scanResult = binding.txtScanResult

        barcodeDetector = BarcodeDetector.Builder(this.requireContext())
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val windowHeight = metrics.heightPixels
        val windowWidth = metrics.widthPixels

        cameraSource = CameraSource.Builder(this.requireContext(), barcodeDetector)
            .setRequestedPreviewSize(windowWidth, windowHeight)
            .setAutoFocusEnabled(true)
            .build()

        qrCodeScanner.holder.addCallback(object : SurfaceHolder.Callback {

            override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
                if (ActivityCompat.checkSelfPermission(
                        this@QRCodeScannerFragment.requireContext(), Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {

                    try {
                        runWithPermissions(Manifest.permission.CAMERA) {

                            cameraSource.start(surfaceHolder)
                            i("Eltin_QRCodeF", "好啦准你開相機")
                            i("Eltin_QRCodeF",
                                "window width = $windowWidth , " +
                            "window height = $windowHeight")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                        cameraSource.start(surfaceHolder)
                        i("Eltin_QRCodeF", "VIP pass 相機直接開起來")
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {

                val qrcode: SparseArray<Barcode>? = detections?.detectedItems
                if (qrcode?.size != 0) {
                    scanResult.post(
                        Runnable {
                            scanResult.text = qrcode?.valueAt(0)?.displayValue
                            // get level and card Id of the restaurant from QR code
                            OwasteRepository._currentQRCodeLevel.value =
                                qrcode?.valueAt(0)?.displayValue?.get(6).toString()
                            OwasteRepository._currentQRCodeCardId.value =
                                qrcode?.valueAt(0)?.displayValue?.substring(15)
                            i("Eltin_QRCodeF", "OwasteRepository._currentQRCodeLevel.value = ${OwasteRepository._currentQRCodeLevel.value}")
                            i("Eltin_QRCodeF", "OwasteRepository._currentQRCodeCardId.value = ${OwasteRepository._currentQRCodeCardId.value}")
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
