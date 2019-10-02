package com.epoch.owaste


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epoch.owaste.databinding.FragmentRestaurantLevelInfoBinding

/**
 * A simple [Fragment] subclass.
 */
class RestaurantLevelInfoFragment : Fragment() {

    lateinit var binding: FragmentRestaurantLevelInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRestaurantLevelInfoBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
}
