package com.ebhs.rtdbworker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ebhs.rtdbworker.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getValueButton.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                val data = RtdbWorker.tryGetData()
                Snackbar.make(view, "${data}", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.startServiceButton.setOnClickListener {
            FgService.start(this.requireContext())
            Snackbar.make(view, "Service started", Snackbar.LENGTH_LONG).show()
        }

        binding.stopServiceButton.setOnClickListener {
            FgService.stop(this.requireContext())
            Snackbar.make(view, "Service stopped", Snackbar.LENGTH_LONG).show()
        }

        binding.startWorkerButton.setOnClickListener {
            RtdbWorker.scheduleWorker(this.requireContext())
            Snackbar.make(view, "Worker started", Snackbar.LENGTH_LONG).show()
        }

        binding.stopWorkerButton.setOnClickListener {
            RtdbWorker.stopWorker(this.requireContext())
            Snackbar.make(view, "Worker started", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}