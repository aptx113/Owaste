package com.epoch.owaste.qrcode


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log.i
import android.util.SparseArray
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.util.size
import androidx.navigation.fragment.findNavController
import com.epoch.owaste.R
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
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    try {
                        runWithPermissions(Manifest.permission.CAMERA) {

                            cameraSource.start(surfaceHolder)
                            i(TAG, "好啦准你開相機")
                            i(
                                TAG, "window width = $windowWidth , " +
                                        "window height = $windowHeight"
                            )
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {

                    cameraSource.start(surfaceHolder)
                    i(TAG, "VIP pass 相機直接開起來")
                }
            }

            override fun surfaceChanged(
                surfaceHolder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
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

                        barcodeDetector.release()

                        val result: String? = qrcode?.valueAt(0)?.displayValue

                        // get level, card Id and name of the restaurants from QR code
                        val levelResult = result?.let {
                            it.substring(it.indexOf("b") + 1, it.indexOf("c"))
                        }

                        val cardIdResult = result?.let {
                            it.substring(it.indexOf("a") + 1, it.indexOf("b"))
                        }

                        val nameResult = result?.let {
                            it.substring(it.indexOf("c") + 1)
                        }

                        OwasteRepository.let {
                            it.getCurrentQRCodeLevel(levelResult)
                            it.getCurrentQRCodeCardId(cardIdResult)
                            it.getCurrentQRCodeRestaurantName(nameResult)
                            it.onQRCodeScannedUpdateCard()
                            it.onQRCodeScannedUpdateExp()
                        }

                        Handler().postDelayed(

                            Runnable {
                                this@QRCodeScannerFragment.findNavController().navigate(R.id.action_global_mapsFragment)
                                i(TAG, "delay")
                            },
                            2500
                        )
                    }
                    )
                }
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }
}
