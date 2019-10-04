package com.epoch.owaste


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log.i
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.epoch.owaste.databinding.FragmentRestaurantLevelInfoDialogBinding
import kotlin.math.max
import kotlin.math.min


/**
 * A simple [Fragment] subclass.
 */
class RestaurantLevelInfoDialogFragment : DialogFragment() {

    val TAG = "Eltin_" + this::class.java.simpleName
    lateinit var binding: FragmentRestaurantLevelInfoDialogBinding
    lateinit var levelInfoTable: ImageView
    var scaleFactor: Float = 1.0f
    var scaleGestureDetector: ScaleGestureDetector? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRestaurantLevelInfoDialogBinding.inflate(inflater, container, false)

        levelInfoTable = binding.imgTableInfo
        scaleGestureDetector = object : ScaleGestureDetector(this.requireContext(), ScaleListener()) {}
        if (scaleGestureDetector != null) {

            binding.root.setOnTouchListener { _, event ->

                i(TAG, "root touch")
                (scaleGestureDetector as ScaleGestureDetector).onTouchEvent(event)
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            val a = RestaurantLevelInfoDialogFragment()

            a.scaleFactor *= a.scaleGestureDetector?.scaleFactor ?: 1.0f
            a.scaleFactor = max(0.1f, min(a.scaleFactor, 10.0f))
            a.levelInfoTable.scaleX = a.scaleFactor
            a.levelInfoTable.scaleY = a.scaleFactor

            return true
        }
    }
}
