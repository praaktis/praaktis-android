package com.mobile.gympraaktis.ui.main.view

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.databinding.FragmentProfileBinding
import com.mobile.gympraaktis.domain.entities.CountryItemDTO
import com.mobile.gympraaktis.domain.entities.UserDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.main.vm.ProfileViewModel
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import timber.log.Timber

class ProfileFragment(override val layoutId: Int = R.layout.fragment_profile) :
    BaseFragment<FragmentProfileBinding>() {

    companion object {
        const val TAG: String = "ProfileFragment"
    }

    override val mViewModel: ProfileViewModel by viewModels()

    private lateinit var oldUserInfo: UserDTO
    private lateinit var newUserInfo: UserDTO

    private var selectedCountry: CountryItemDTO? = null
    private lateinit var countryPicker: CountryPicker

    private var userImageUri: Uri? = null

    private val pickerLauncher = registerImagePicker { images ->
        if (images.isNotEmpty()) {
            userImageUri = images.first().uri
            Glide.with(this)
                .load(userImageUri)
                .centerCrop()
                .into(binding.ivAvatar)
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.profileInfoEvent.observe(this) {
            setProfileCredentials(it)
        }

        ivAvatar.onClick {
            pickerLauncher.launch(IMAGE_PICKER_CONFIG)
        }

        mViewModel.countriesEvent.observe(this) { countries ->
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

        mViewModel.updateProfileEvent.observe(this) {
            activity.makeToast(it)
            oldUserInfo = newUserInfo
        }

    }

    private fun setProfileCredentials(user: UserDTO) {
        binding.ivAvatar.loadAvatar(user.imageUrl, R.drawable.ic_user_large)
        etFirstName.setText(user.firstName)
        etLastName.setText(user.lastName)
        etNickname.setText(user.nickname)
        if (user.country != null)
            etCountry.setText(mViewModel.getCountryObject()?.name)
        etEmail.setText(user.email)
        initValidation(user)
    }

    private fun initValidation(user: UserDTO) {
        selectedCountry = mViewModel.getCountryObject()
        oldUserInfo = UserDTO(
            firstName = etFirstName.stringText(),
            lastName = etLastName.stringText(),
            nickname = etNickname.stringText(),
            country = mViewModel.getCountryObject()?.key,
            password = etPassword.stringText()
        )

        form {
            inputLayout(tilFirstName) {
                isNotEmpty()
            }
            inputLayout(tilLastName) {
                isNotEmpty()
            }
            inputLayout(tilNickname) {
                isNotEmpty()
            }
            inputLayout(tilCountry) {
                isNotEmpty()
            }
            inputLayout(tilPassword, optional = true) {
                startRealTimeValidation(500)
                length().atLeast(8).description(getString(R.string.at_least_8_characters))
                conditional({ !binding.etConfirmPassword.text.isNullOrBlank() }) {
                    assert(getString(R.string.password_donot_match)) {
                        binding.etConfirmPassword.text.toString() == binding.etPassword.text.toString()
                    }
                }
            }
            inputLayout(binding.tilConfirmPassword) {
                startRealTimeValidation(500)
                conditional({ !binding.etPassword.text.isNullOrBlank() }) {
                    length().atLeast(8).description(getString(R.string.at_least_8_characters))
                    assert(getString(R.string.password_donot_match)) {
                        binding.etConfirmPassword.text.toString() == binding.etPassword.text.toString()
                    }
                }
            }
            submitWith(tvSave) {
                newUserInfo = UserDTO(
                    firstName = etFirstName.stringText(),
                    lastName = etLastName.stringText(),
                    nickname = etNickname.stringText(),
                    country = selectedCountry?.key,
                    password = etPassword.stringText(),
//                    profileImage = userImageUri
                )

                val diffUser = returnDiffUserDTO()
                if (JSONObject(Gson().toJson(diffUser)).length() == 0)
                    activity.makeToast("no changes")
                else
                    mViewModel.updateProfile(diffUser, userImageUri)

                Timber.d("DIFFUSER " + Gson().toJson(diffUser))
            }
        }
    }

    private fun returnDiffUserDTO(): UserDTO {
        return UserDTO(
            dateOfBirth = oldUserInfo.dateOfBirth.returnDiff(newUserInfo.dateOfBirth),
            firstName = oldUserInfo.firstName!!.returnDiff(newUserInfo.firstName),
            lastName = oldUserInfo.lastName!!.returnDiff(newUserInfo.lastName),
            nickname = oldUserInfo.nickname!!.returnDiff(newUserInfo.nickname),
            password = oldUserInfo.password!!.returnDiff(newUserInfo.password),
            country = oldUserInfo.country.toString().returnDiff(newUserInfo.country.toString()),
            profileImage = userImageUri?.toString()
        )
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

        etCountry.onClick {
            countryPicker.showBottomSheet(activity)
        }
    }

}

fun String?.returnDiff(compare: String?): String? {
    return if (this == compare) null else compare
}