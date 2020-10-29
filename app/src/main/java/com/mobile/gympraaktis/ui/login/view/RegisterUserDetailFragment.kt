package com.mobile.gympraaktis.ui.login.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentRegisterUserDetailBinding
import com.mobile.gympraaktis.domain.entities.CountryItemDTO
import com.mobile.gympraaktis.domain.entities.Gender
import com.mobile.gympraaktis.domain.entities.UserDTO
import com.mobile.gympraaktis.domain.entities.UserLevel
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.login.vm.RegisterUserDetailViewModel
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_register_user_detail.*
import org.threeten.bp.LocalDate
import java.util.*


class RegisterUserDetailFragment constructor(override val layoutId: Int = R.layout.fragment_register_user_detail) :
    BaseFragment<FragmentRegisterUserDetailBinding>() {

    companion object {
        const val TAG: String = "RegisterUserDetailFragment"
        fun getInstance() = RegisterUserDetailFragment()
    }

    override val mViewModel: RegisterUserDetailViewModel by viewModels()

    private var dateOfBirthCal: GregorianCalendar? = null
    private lateinit var user: UserDTO

    private var selectedCountry: CountryItemDTO? = null
    private var countryPicker: CountryPicker? = null

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
            inputLayout(tilFirstName) {
                isNotEmpty().description("Enter your First Name")
            }
            inputLayout(tilLastName) {
                isNotEmpty().description("Enter your Last Name")
            }
            inputLayout(tilDateOfBirth) {
                isNotEmpty().description("Set your date of birth")
            }
            inputLayout(tilCountry) {
                isNotEmpty().description("Choose country")
            }
            inputLayout(tilUsername) {
                isNotEmpty().description("Enter your nickname")
            }
            submitWith(tvNextStep) {
                activity.hideKeyboard(tvNextStep)
                user = UserDTO(
                    ability = userLevel,
                    gender = Gender.values()[resources.getStringArray(R.array.genders)
                        .indexOf(binding.etGender.text.toString())],
                    firstName = etFirstName.stringText(),
                    lastName = etLastName.stringText(),
                    nickname = etUsername.stringText(),
                    country = selectedCountry?.key,
                    dateOfBirth = dateOfBirthCal?.dateYYYY_MM_DD(),
                    profileImage = userImageUri
                )

                mViewModel.updateProfile(user)
            }
        }

        mViewModel.countriesEvent.observe(this, androidx.lifecycle.Observer { countries ->
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
        })

        mViewModel.getCountries()

        mViewModel.updateProfileEvent.observe(this, androidx.lifecycle.Observer {
            activity.makeToast(it)
            val tag = AcceptTermsFragment.TAG
            activity.showOrReplace(tag) {
                add(
                    R.id.container,
                    AcceptTermsFragment.getInstance(),
                    tag
                ).addToBackStack(tag)
            }
        })

        initCountryPicker(null)
    }

    private fun initCountryPicker(customCountryList: Array<Country>?) {
        val countryPickerBuilder = CountryPicker.Builder().with(context!!).listener {
            selectedCountry = CountryItemDTO(it.code, it.name)
            etCountry.setText(it.name)
            activity.hideKeyboard(etCountry)
            activity.hideKeyboard(tilCountry)
        }
        customCountryList?.let { countryPickerBuilder.setCountryList(it) }
        countryPicker = countryPickerBuilder.build()
    }

    private var userLevel = UserLevel.B

    private var userImageUri: String? = null

    private fun initClicks() {

        etCountry.onClick {
            countryPicker?.showBottomSheet(activity)
        }

        ivAvatar.onClick {
            openImagePicker()
        }

        tvBackLogin.onClick {
            activity.supportFragmentManager.popBackStack()
            activity.supportFragmentManager.popBackStack()
        }

        etDateOfBirth.onClick {
            showBirthdayDialog()
        }

        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            userLevel = when (checkedId) {
                R.id.btn_beginner -> UserLevel.B
                R.id.btn_intermediate -> UserLevel.I
                R.id.btn_expert -> UserLevel.E
                else -> UserLevel.E
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            val images: ArrayList<Image> = ImagePicker.getImages(data)
            userImageUri = images.first().path
            Glide.with(this)
                .load(userImageUri)
                .into(ivAvatar)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}