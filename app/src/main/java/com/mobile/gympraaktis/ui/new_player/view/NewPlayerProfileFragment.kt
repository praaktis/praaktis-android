package com.mobile.gympraaktis.ui.new_player.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentNewPlayerProfileBinding
import com.mobile.gympraaktis.domain.entities.GenderDTO
import com.mobile.gympraaktis.domain.entities.KeyValueDTO
import com.mobile.gympraaktis.domain.entities.PlayerCreateModel
import com.mobile.gympraaktis.domain.entities.UserLevel
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.main.view.MainActivity
import com.mobile.gympraaktis.ui.new_player.vm.NewPlayerProfileViewModel
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewPlayerProfileFragment(override val layoutId: Int = R.layout.fragment_new_player_profile) :
    BaseFragment<FragmentNewPlayerProfileBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            NewPlayerProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }

        const val TAG = "NewPlayerProfileFragment"
    }

    override val mViewModel: NewPlayerProfileViewModel by viewModels()

    private lateinit var user: PlayerCreateModel

    private var userImageUri: String? = null

    override fun initUI(savedInstanceState: Bundle?) {

        initClicks()

        setupAbilityField()
        setupAgeField()
        setupHeightField()
        setupWeightField()
        setupGenderField()

        form {
            inputLayout(binding.tilGender) {
                isNotEmpty().description("Select your gender")
            }
            inputLayout(binding.tilName) {
                isNotEmpty().description("Enter your Name")
            }
            inputLayout(binding.tilUsername) {
                isNotEmpty().description("Enter your Nickname")
            }
            inputLayout(binding.tilAgeRange) {
                isNotEmpty().description("Select your age range")
            }
            inputLayout(binding.tilHeightRange) {
                isNotEmpty().description("Select your height range")
            }
            inputLayout(binding.tilWeightRange) {
                isNotEmpty().description("Select your weight range")
            }
            submitWith(binding.btnCreate) {
                activity.hideKeyboard(binding.btnCreate)
                user = PlayerCreateModel(
                    gender = (binding.etGender.tag as GenderDTO?)?.key,
                    playerName = binding.etName.stringText(),
                    nickname = binding.etUsername.stringText(),
                    weightRange = (binding.etWeightRange.tag as KeyValueDTO?)?.key,
                    heightRange = (binding.etHeightRange.tag as KeyValueDTO?)?.key,
                    ageGroup = (binding.etAgeRange.tag as KeyValueDTO?)?.key,
                    ability = binding.tgAbility.tag as UserLevel?,
                    udf1 = "",
                    udf2 = ""
                )
                mViewModel.createPlayer(user)
            }
        }

        mViewModel.createPlayerEvent.observe(viewLifecycleOwner) {
            activity.makeToast(it)
            lifecycleScope.launch {
                delay(500)
                (activity as MainActivity).backToDashboardAndRefresh()
            }
        }
    }

    private fun setupAbilityField() {
        binding.tgAbility.tag = when (binding.tgAbility.checkedButtonId) {
            R.id.btn_beginner -> UserLevel.B
            R.id.btn_intermediate -> UserLevel.I
            R.id.btn_expert -> UserLevel.E
            else -> UserLevel.B
        }
        binding.tgAbility.addOnButtonCheckedListener { group, checkedId, isChecked ->
            binding.tgAbility.tag = when (checkedId) {
                R.id.btn_beginner -> UserLevel.B
                R.id.btn_intermediate -> UserLevel.I
                R.id.btn_expert -> UserLevel.E
                else -> UserLevel.B
            }
        }
    }

    private fun setupAgeField() {
        var items = emptyList<KeyValueDTO>()
        mViewModel.ageLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etAgeRange.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etAgeRange.setOnItemClickListener { _, _, position, _ ->
            binding.etAgeRange.tag = items.getOrNull(position)
        }
    }

    private fun setupHeightField() {
        var items = emptyList<KeyValueDTO>()
        mViewModel.heightLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etHeightRange.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etHeightRange.setOnItemClickListener { _, _, position, _ ->
            binding.etHeightRange.tag = items.getOrNull(position)
        }
    }

    private fun setupWeightField() {
        var items = emptyList<KeyValueDTO>()
        mViewModel.weightLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etWeightRange.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etWeightRange.setOnItemClickListener { _, _, position, _ ->
            binding.etWeightRange.tag = items.getOrNull(position)
        }
    }

    private fun setupGenderField() {
        var items = emptyList<GenderDTO>()
        mViewModel.genderLiveData.observe(viewLifecycleOwner) { list ->
            items = list
            binding.etGender.setSimpleItems(list.map { it.name }.toTypedArray())
        }

        binding.etGender.setOnItemClickListener { _, _, position, _ ->
            binding.etGender.tag = items.getOrNull(position)
        }
    }

    private val pickerLauncher = registerImagePicker { images ->
        if (images.isNotEmpty()) {
            userImageUri = images.first().uri.path
            Glide.with(this)
                .load(images.first().uri)
                .centerCrop()
                .into(binding.ivAvatar)
        }
    }

    private fun initClicks() {
        binding.ivAvatar.onClick {
            pickerLauncher.launch(IMAGE_PICKER_CONFIG)
        }
    }

}