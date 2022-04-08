package com.mobile.gympraaktis.domain.common.custom_ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.AdapterView
import com.google.android.material.card.MaterialCardView
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.databinding.LayoutRoundedDropdownBinding
import com.mobile.gympraaktis.domain.extension.dp

class RoundedDropdownView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding: LayoutRoundedDropdownBinding =
        LayoutRoundedDropdownBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedDropdownView, 0, 0).apply {
            try {
                binding.et.hint = getString(R.styleable.RoundedDropdownView_hint)
            } finally {
                recycle()
            }
        }

        binding.et.setDropDownBackgroundResource(R.drawable.shape_popup_menu)
        binding.et.dropDownVerticalOffset = 8.dp
    }

    private var dropdownItems: Array<String> = emptyArray()

    fun setDropdownValues(list: Array<String>) {
        dropdownItems = list.copyOf()
        binding.et.setSimpleItems(dropdownItems)
    }

    fun setText(text: String) {
        binding.et.setText(text)
    }

    fun setOnItemClickListener(l: AdapterView.OnItemClickListener) {
        binding.et.onItemClickListener = l
    }

}