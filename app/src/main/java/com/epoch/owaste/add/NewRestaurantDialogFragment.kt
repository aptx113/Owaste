package com.epoch.owaste.add


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment

import com.epoch.owaste.R
import com.epoch.owaste.databinding.FragmentNewRestaurantDialogBinding
import kotlinx.android.synthetic.main.fragment_new_restaurant_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class NewRestaurantDialogFragment : DialogFragment() {

    lateinit var binding: FragmentNewRestaurantDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.add_new_restaurant_dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.setBackgroundDrawableResource(R.color.transparent_CC000000)
        binding = FragmentNewRestaurantDialogBinding.inflate(inflater, container, false)

        binding.imgExpand.setOnClickListener {
            i("Eltin_", "expand open time!")
            cb_open_time_mon.visibility = View.VISIBLE
            cb_open_time_tue.visibility = View.VISIBLE
            cb_open_time_wed.visibility = View.VISIBLE
            cb_open_time_thu.visibility = View.VISIBLE
            cb_open_time_fri.visibility = View.VISIBLE
            cb_open_time_sat.visibility = View.VISIBLE
            cb_open_time_sun.visibility = View.VISIBLE
        }

        binding.imgClose.setOnClickListener {
            i("Eltin_", "close dialog!")
            dismiss()
        }

        binding.btnCommit.setOnClickListener {
            if (
                binding.edittxtNewAddress.text.isNotEmpty() &&
                binding.edittxtNewName.text.isNotEmpty() &&
                binding.edittxtNewTel.text.isNotEmpty()
            ) {
                Toast.makeText(
                    this.context,
                    getString(R.string.on_commit_new_restaurant),
                    Toast.LENGTH_SHORT
                ).show()
                i("Eltin_", "new restaurant committed !")
                this.dismiss()
            } else {
                Toast.makeText(
                    this.context,
                    getString(R.string.on_commit_new_restaurant_error),
                    Toast.LENGTH_SHORT
                ).show()
                i("Eltin_", "new resaurant committed error!")
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}
