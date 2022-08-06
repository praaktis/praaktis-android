package com.mobile.gympraaktis.ui.players

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.databinding.FragmentMyPlayersBinding
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.entities.*
import com.mobile.gympraaktis.domain.extension.makeToast
import com.mobile.gympraaktis.domain.extension.stringText
import com.mobile.gympraaktis.ui.new_player.vm.NewPlayerProfileViewModel

class MyPlayersFragment(override val layoutId: Int = R.layout.fragment_my_players) :
    BaseFragment<FragmentMyPlayersBinding>() {

    companion object {
        const val TAG: String = "MyPlayersFragment"
        fun newInstance() = MyPlayersFragment()
    }

    override val mViewModel: MyPlayersViewModel by viewModels()

    private val newPlayerProfileViewModel: NewPlayerProfileViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {

        mViewModel.observePlayers().observe(viewLifecycleOwner) { players ->

            if(binding.dropdownSelectPlayer.tag == null) {
                val defaultPlayerId = players.firstOrNull()?.id ?: -1L
                if (defaultPlayerId != -1L) {
                    players.find {
                        it.id == defaultPlayerId
                    }?.let {
                        binding.dropdownSelectPlayer.setText(it.name)
                        binding.dropdownSelectPlayer.tag = it
                        mViewModel.getPlayerProfile(it.id)
                    }
                }
            }


            binding.dropdownSelectPlayer.setDropdownValues(players.map { it.name }.toTypedArray())
            binding.dropdownSelectPlayer.setOnItemClickListener { parent, view, position, id ->
                players.getOrNull(position)?.let {
                    binding.dropdownSelectPlayer.tag = it
                    SettingsStorage.instance.setSelectedPlayerId(it.id)
                    mViewModel.getPlayerProfile(it.id)
                }
            }
        }

        mViewModel.playerResultEvent.observe(viewLifecycleOwner) {
            setPlayerDetails(it)
        }

        setupAbilityField()
        setupAgeField()
        setupHeightField()
        setupWeightField()
        setupGenderField()

        binding.tvSave.setOnClickListener {
            kotlin.runCatching {
                PlayerUpdateModel(
                    playerId = (binding.dropdownSelectPlayer.tag as PlayerEntity).id,
                    gender = (binding.etGender.tag as GenderDTO?)?.key,
                    playerName = binding.etName.stringText(),
                    nickname = binding.etUsername.stringText(),
                    weightRange = (binding.etWeightRange.tag as KeyValueDTO?)?.key,
                    heightRange = (binding.etHeightRange.tag as KeyValueDTO?)?.key,
                    ageGroup = (binding.etAgeRange.tag as KeyValueDTO?)?.key,
                    ability = binding.tilLevel.tag as UserLevel?,
                    udf1 = "",
                    udf2 = ""
                )
            }.onSuccess {
                mViewModel.updatePlayer(it)
            }.onFailure {
                activity.makeToast("Error")
            }
        }

        mViewModel.updatePlayerEvent.observe(viewLifecycleOwner) {
            activity.makeToast(it)
        }

    }

    private fun setPlayerDetails(player: PlayerDTO) {
        binding.etName.setText(player.playerName)
        binding.etUsername.setText(player.nickname)
        binding.etLevel.apply {
            setText(player.ability?.ability ?: "", false)
            tag = player.ability?.key
        }
        binding.etAgeRange.apply {
            setText(player.ageGroup?.name ?: "", false)
            tag = player.ageGroup
        }
        binding.etWeightRange.apply {
            setText(player.weightRange?.name ?: "", false)
            tag = player.weightRange
        }
        binding.etHeightRange.apply {
            setText(player.heightRange?.name ?: "", false)
            tag = player.heightRange
        }
        binding.etGender.apply {
            setText(player.gender?.name ?: "", false)
            tag = player.gender
        }
    }

    private fun setupAbilityField() {
        val items = UserLevel.values()
        binding.etLevel.setSimpleItems(items.map { it.label }.toTypedArray())
        binding.etLevel.setOnItemClickListener { _, _, position, _ ->
            binding.etLevel.tag = items.getOrNull(position)
        }
    }

    private fun setupAgeField() {
        var items = emptyList<KeyValueDTO>()
        newPlayerProfileViewModel.ageLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etAgeRange.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etAgeRange.setOnItemClickListener { _, _, position, _ ->
            binding.etAgeRange.tag = items.getOrNull(position)
        }
    }

    private fun setupHeightField() {
        var items = emptyList<KeyValueDTO>()
        newPlayerProfileViewModel.heightLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etHeightRange.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etHeightRange.setOnItemClickListener { _, _, position, _ ->
            binding.etHeightRange.tag = items.getOrNull(position)
        }
    }

    private fun setupWeightField() {
        var items = emptyList<KeyValueDTO>()
        newPlayerProfileViewModel.weightLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etWeightRange.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etWeightRange.setOnItemClickListener { _, _, position, _ ->
            binding.etWeightRange.tag = items.getOrNull(position)
        }
    }

    private fun setupGenderField() {
        var items = emptyList<GenderDTO>()
        newPlayerProfileViewModel.genderLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etGender.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etGender.setOnItemClickListener { _, _, position, _ ->
            binding.etGender.tag = items.getOrNull(position)
        }
    }

}
