package edu.sapi.justpongapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import edu.sapi.justpongapp.R
import edu.sapi.justpongapp.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    companion object {
        val TAG = GameFragment::class.simpleName
    }

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val _vm: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnResumePause.setOnClickListener{ (it as Button)

            if (it.text.toString() == resources.getString(R.string.pause)) {
                Log.d(TAG, "Paused")
                _vm.pauseGame()
                it.text = resources.getString(R.string.resume)
            }
            else
            {
                Log.d(TAG, "Resumed")
                _vm.resumeGame()
                it.text = resources.getString(R.string.pause)
            }
        }

        _vm.startGame(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vm.closeWebSocket()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        _vm.pauseMovementService()
    }

    override fun onResume() {
        super.onResume()
        _vm.resumeMovementService()
    }


}