package com.mobile.gympraaktis.ui.new_player.view

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentNewPlayerProfileBinding
import com.mobile.gympraaktis.domain.entities.CountryItemDTO
import com.mobile.gympraaktis.domain.entities.Gender
import com.mobile.gympraaktis.domain.entities.UserDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.new_player.vm.NewPlayerProfileViewModel
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_register_user_detail.*
import org.threeten.bp.LocalDate
import java.util.*

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

    private var dateOfBirthCal: GregorianCalendar? = null
    private lateinit var user: UserDTO

    private var selectedCountry: CountryItemDTO? = null
    private var countryPicker: CountryPicker? = null

    private var userImageUri: String? = null


    override fun initUI(savedInstanceState: Bundle?) {

        initClicks()

        val genderAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            context?.resources?.getStringArray(R.array.genders)!!
        )
        binding.etGender.setAdapter(genderAdapter)


        form {
            inputLayout(binding.tilGender) {
                isNotEmpty().description("Select your gender")
            }
            inputLayout(binding.tilFirstName) {
                isNotEmpty().description("Enter your First Name")
            }
            inputLayout(binding.tilLastName) {
                isNotEmpty().description("Enter your Last Name")
            }
            inputLayout(binding.tilDateOfBirth) {
                isNotEmpty().description("Set your date of birth")
            }
            inputLayout(binding.tilCountry) {
                isNotEmpty().description("Choose country")
            }
            inputLayout(binding.tilUsername) {
                isNotEmpty().description("Enter your nickname")
            }
            submitWith(binding.btnCreate) {
                activity.hideKeyboard(binding.btnCreate)
                user = UserDTO(
                    gender = Gender.values()[resources.getStringArray(R.array.genders)
                        .indexOf(binding.etGender.text.toString())],
                    firstName = etFirstName.stringText(),
                    lastName = etLastName.stringText(),
                    nickname = etUsername.stringText(),
                    country = selectedCountry?.key,
                    dateOfBirth = dateOfBirthCal?.dateYYYY_MM_DD(),
                    profileImage = userImageUri
                )

                mViewModel.createPlayer(user)
            }
        }


        mViewModel.countriesEvent.observe(viewLifecycleOwner) { countries ->
            val countryArray: Array<Country> = Array(countries.size) { index ->
                val countryMatch = CountryPicker.COUNTRIES.find { it.code == countries[index].key }
                Country(
                    countries[index].key,
                    countries[index].name,
                    countryMatch?.dialCode,
                    countryMatch?.flag ?: R.drawable.empty_flag,
                    countryMatch?.currency
                )
            }
            initCountryPicker(countryArray)
        }

        mViewModel.getCountries()

        initCountryPicker(null)
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

        binding.etCountry.onClick {
            countryPicker?.showBottomSheet(activity)
        }

        binding.ivAvatar.onClick {
            pickerLauncher.launch(IMAGE_PICKER_CONFIG)
        }

        etDateOfBirth.onClick {
            showBirthdayDialog()
        }

    }

    private fun initCountryPicker(customCountryList: Array<Country>?) {
        val countryPickerBuilder = CountryPicker.Builder().with(requireContext()).listener {
            selectedCountry = CountryItemDTO(it.code, it.name)
            etCountry.setText(it.name)
            activity.hideKeyboard(etCountry)
            activity.hideKeyboard(tilCountry)
        }
        customCountryList?.let { countryPickerBuilder.setCountryList(it) }
        countryPicker = countryPickerBuilder.build()
    }

    private fun showBirthdayDialog() {
        if (dateOfBirthCal == null) {
            dateOfBirthCal = GregorianCalendar()
        }
        SpinnerDatePickerDialogBuilder()
            .context(requireContext())
            .callback { view, year, monthOfYear, dayOfMonth ->
                dateOfBirthCal?.set(year, monthOfYear, dayOfMonth)
                etDateOfBirth.setText(
                    LocalDate.parse(dateOfBirthCal?.dateYYYY_MM_DD()).formatMMMddYYYY()
                )
            }
            .dialogTheme(R.style.MyDatePickerDialogTheme)
            .spinnerTheme(R.style.MyDatePickerDialogTheme)
            .showTitle(true)
            .showDaySpinner(true)
            .defaultDate(2017, 0, 1)
            .maxDate(2020, 0, 1)
            .minDate(1950, 0, 1)
            .build()
            .show()
    }

    override fun onDestroy() {
        countryPicker = null
        super.onDestroy()
    }
}