package com.mobile.praaktishockey.ui.main.view

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentProfileBinding
import com.mobile.praaktishockey.domain.common.ImageUtils
import com.mobile.praaktishockey.domain.entities.CountryItemDTO
import com.mobile.praaktishockey.domain.entities.Gender
import com.mobile.praaktishockey.domain.entities.UserDTO
import com.mobile.praaktishockey.domain.extension.*
import com.mobile.praaktishockey.ui.main.vm.ProfileViewModel
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import org.threeten.bp.LocalDate
import java.io.File
import java.util.*

class ProfileFragment(override val layoutId: Int = R.layout.fragment_profile) :
    BaseFragment<FragmentProfileBinding>() {

    companion object {
        val TAG: String = ProfileFragment::class.java.simpleName
    }

    override val mViewModel: ProfileViewModel get() = getViewModel { ProfileViewModel(Application()) }

    private lateinit var oldUserInfo: UserDTO
    private lateinit var newUserInfo: UserDTO

    private var dateOfBirthCal: GregorianCalendar? = null

    private var selectedCountry: CountryItemDTO? = null
    private lateinit var countryPicker: CountryPicker

    private var userImageUri: String? = null

    override fun initUI(savedInstanceState: Bundle?) {
        mViewModel.profileInfoEvent.observe(this, Observer {
            setProfileCredentials(it)
        })

        ivAvatar.onClick {
            openImagePicker()
        }

        mViewModel.countriesEvent.observe(this, Observer { countries ->
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

        mViewModel.updateProfileEvent.observe(this, Observer {
            activity.makeToast(it)
            oldUserInfo = newUserInfo
        })


    }

    private fun setProfileCredentials(user: UserDTO) {
        binding.ivAvatar.loadAvatar(user.imageUrl, R.drawable.ic_user_large)
        if (user.dateOfBirth != null)
            etDateOfBirth.setText(LocalDate.parse(user.dateOfBirth).formatMMMddYYYY())
        etFirstName.setText(user.firstName)
        etLastName.setText(user.lastName)
        etNickname.setText(user.nickname)
        if (user.country != null)
            etCountry.setText(mViewModel.getCountryObject()?.name)
        etEmail.setText(user.email)

        if (user.gender != null) {
            val genders = resources.getStringArray(R.array.genders)
            binding.etGender.setText(genders[user.gender.ordinal])
            val genderAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                genders
            )
            binding.etGender.setAdapter(genderAdapter)
        }

        etDateOfBirth.onClick {
            showBirthdayDialog()
        }

        initValidation(user)
    }

    private fun initValidation(user: UserDTO) {
        selectedCountry = mViewModel.getCountryObject()
        oldUserInfo = UserDTO(
            dateOfBirth = user.dateOfBirth?.let { LocalDate.parse(user.dateOfBirth).toString() },
            gender = Gender.values()[resources.getStringArray(R.array.genders)
                .indexOf(binding.etGender.text.toString())],
            firstName = etFirstName.stringText(),
            lastName = etLastName.stringText(),
            nickname = etNickname.stringText(),
            country = mViewModel.getCountryObject()?.key,
            password = etPassword.stringText()
        )

        form {
            inputLayout(tilDateOfBirth) {
                isNotEmpty()
            }
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
                    dateOfBirth = etDateOfBirth.stringText().MMMddYYYYtoLocalDate().toString(),
                    gender = Gender.values()[resources.getStringArray(R.array.genders)
                        .indexOf(binding.etGender.text.toString())],
                    firstName = etFirstName.stringText(),
                    lastName = etLastName.stringText(),
                    nickname = etNickname.stringText(),
                    country = selectedCountry?.key,
                    password = etPassword.stringText(),
                    profileImage = userImageUri
                )

                val diffUser = returnDiffUserDTO()
                if (JSONObject(Gson().toJson(diffUser)).length() == 0)
                    activity.makeToast("no changes")
                else
                    mViewModel.updateProfile(diffUser)

                Log.d("DIFFUSER", Gson().toJson(diffUser))
            }
        }
    }

    private fun returnDiffUserDTO(): UserDTO {
        return UserDTO(
            dateOfBirth = oldUserInfo.dateOfBirth.returnDiff(newUserInfo.dateOfBirth),
            gender = oldUserInfo.gender!!.name.returnDiff(newUserInfo.gender!!.name)
                ?.let { Gender.valueOf(it) },
            firstName = oldUserInfo.firstName!!.returnDiff(newUserInfo.firstName),
            lastName = oldUserInfo.lastName!!.returnDiff(newUserInfo.lastName),
            nickname = oldUserInfo.nickname!!.returnDiff(newUserInfo.nickname),
            password = oldUserInfo.password!!.returnDiff(newUserInfo.password),
            country = oldUserInfo.country.toString().returnDiff(newUserInfo.country.toString()),
            profileImage = userImageUri
        )
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

        etCountry.onClick {
            countryPicker.showBottomSheet(activity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            val images: ArrayList<Image> = ImagePicker.getImages(data)
            userImageUri = images.first().path
            Glide.with(this)
                .load(ImageUtils.convertToBitmap2(File(userImageUri), 300, 300))
                .into(ivAvatar)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showBirthdayDialog() {
        if (dateOfBirthCal == null) {
            dateOfBirthCal = GregorianCalendar()
        }

        var initDate = LocalDate.now()
        if (etDateOfBirth.isNotEmpty()) {
            initDate = etDateOfBirth.stringText().MMMddYYYYtoLocalDate()
        }

//        val datePicker = DatePickerDialog(
//            Objects.requireNonNull<FragmentActivity>(activity),
//            R.style.MyDatePickerDialogTheme,
//            { view, year, monthOfYear, dayOfMonth -> },
//            initDate.year,
//            initDate.monthValue - 1,
//            initDate.dayOfMonth
//        )
//        datePicker.datePicker.maxDate = System.currentTimeMillis()
//        datePicker.show()
//        val ok = datePicker.getButton(AlertDialog.BUTTON_POSITIVE)
//        ok.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
//        ok.setTextColor(ContextCompat.getColor(context!!, R.color.main_bg))
//        datePicker.getButton(AlertDialog.BUTTON_NEGATIVE)
//            .setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
//        datePicker.getButton(AlertDialog.BUTTON_NEGATIVE)
//            .setTextColor(ContextCompat.getColor(context!!, R.color.main_bg))
//        ok.setOnClickListener { v ->
//            dateOfBirthCal?.set(Calendar.YEAR, datePicker.datePicker.year)
//            dateOfBirthCal?.set(Calendar.MONTH, datePicker.datePicker.month)
//            dateOfBirthCal?.set(Calendar.DAY_OF_MONTH, datePicker.datePicker.dayOfMonth)
//
//            etDateOfBirth.setText(LocalDate.parse(dateOfBirthCal?.dateYYYY_MM_DD()).formatMMMddYYYY())
//            datePicker.dismiss()
//        }

        val picker = SpinnerDatePickerDialogBuilder()
            .context(context!!)
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
            .defaultDate(initDate.year, initDate.monthValue, initDate.dayOfMonth)
            .maxDate(2020, 0, 1)
            .minDate(1950, 0, 1)
            .build()

        picker.show()
    }

}

fun String?.returnDiff(compare: String?): String? {
    return if (this == compare) null else compare
}