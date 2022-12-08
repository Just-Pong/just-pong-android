package edu.sapi.justpongapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.sapi.justpongapp.R
import edu.sapi.justpongapp.databinding.FragmentStartGameBinding


class StartGameFragment : Fragment() {

    private var _binding: FragmentStartGameBinding? = null
    private val binding get() = _binding!!

    private val _vm: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            if (binding.etGameId.text.isEmpty())
                return@setOnClickListener

            val gameId = binding.etGameId.text.toString()
            _vm.setGameId(gameId)

            findNavController().navigate(R.id.action_fragment_start_game_to_fragment_game)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}