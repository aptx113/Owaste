package com.epoch.owaste.card


import android.os.Bundle
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.epoch.owaste.R
import com.epoch.owaste.databinding.FragmentRewardCardBinding

/**
 * A simple [Fragment] subclass.
 */
class RewardCardFragment : Fragment() {

    private lateinit var viewModel: RewardCardViewModel
    private lateinit var binding: FragmentRewardCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(RewardCardViewModel::class.java)
        binding = FragmentRewardCardBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.rvRewardCard.adapter = RewardCardAdapter(viewModel)


        binding.imgBack.setOnClickListener {

        }
        // Inflate the layout for this fragment
        return binding.root
    }


}
