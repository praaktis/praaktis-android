@file:JvmName("ViewExt")

package com.mobile.gympraaktis.domain.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.*
import android.view.animation.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.appcompat.content.res.AppCompatResources
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

fun TextView.isEmpty(): Boolean = TextUtils.isEmpty(this.text.toString())

fun TextView.isNotEmpty(): Boolean = TextUtils.isEmpty(this.text.toString()).not()
fun View.show() {
    this.visibility = (View.VISIBLE)
}

fun View.showIf(condition: Boolean) {
    if (condition) this.visibility = (View.VISIBLE)
}

fun View.manageVisibility(condition: Boolean) {
    if (condition) this.visibility = (View.VISIBLE)
    else visibility = View.GONE
}

fun View.hide() {
    this.visibility = (View.GONE)
}

fun View.invisible() {
    this.visibility = (View.INVISIBLE)
}

fun View.isVisible(): Boolean {
    return this.visibility == (View.VISIBLE)
}

fun View.isNotVisible(): Boolean {
    return this.visibility != (View.VISIBLE)
}

fun View.toggleVisibility() = if (isVisible()) hide() else show()

@JvmOverloads
fun View.showAnim(duration: Long = 200) {
    this.alpha = 0f
    this.visibility = View.VISIBLE
    this.animate()
        .setDuration(duration)
        .alpha(1f)
        .setListener(null)
}

fun View.onClick(listener: View.OnClickListener) {
    this.setOnClickListener { listener.onClick(this) }
}

fun View.onClick(listener: (View) -> Unit) {
    this.setOnClickListener { listener.invoke(it) }
}

fun View.lock() {
    isEnabled = false
    isClickable = false
}

fun View.unlock() {
    isEnabled = true
    isClickable = true
}

fun ViewGroup.lockAllChildren() {
    views().forEach { it.lock() }
}

fun ViewGroup.unlockAllChildren() {
    views().forEach { it.unlock() }
}

fun View.doOnPreDraw(callback: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            callback.invoke()
            return true
        }
    })
}

operator fun ViewGroup.get(pos: Int): View = getChildAt(pos)

fun ViewGroup.views(): List<View> {
    return (0 until childCount).map { getChildAt(it) }
}

inline fun View.snack(message: String, f: Snackbar.() -> Unit, length: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

inline fun View.snack(resId: Int, f: Snackbar.() -> Unit, @IdRes length: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, resId, length)
    snack.f()
    snack.show()
}

fun TextView.stringText() = this.text.toString()
fun TextView.clearText() {
    this.text = ""
}

fun EditText.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()
}

fun EditText.stringText() = this.text.trim().toString()

fun EditText.clearText() {
    text.clear()
}

fun EditText.moveCursorToEnd() {
    if (text.isNotEmpty()) setSelection(text.length)
}

fun View.showKeyBord() {
    this.requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.showKeyBoardImplicit() {
    this.requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun EditText.getString() = this.text.toString()

inline fun View.waitForLayout(crossinline f: () -> Unit) = with(viewTreeObserver) {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            removeOnGlobalLayoutListener(this)
            f()
        }
    })
}

fun ViewGroup.transition(duration: Long = 200, transition: Transition = ChangeBounds()) {
    transition.duration = duration
    TransitionManager.beginDelayedTransition(this, transition)
}

fun View.getCurrentBackgroundColor(): Int? = (this.background as? ColorDrawable)?.color

fun View.animateColorWhatever(duration: Long = 500L, colorFrom: Int, colorTo: Int, animation: (ValueAnimator) -> Unit) {
    val anim = ValueAnimator()
    anim.setIntValues(colorFrom, colorTo)
    anim.setEvaluator(ArgbEvaluator())
    anim.addUpdateListener { valueAnimator -> animation(valueAnimator) }

    anim.duration = duration
    anim.start()
}

fun TextInputLayout.setErrorMess(isValid: Boolean, errorStringId: Int) {
    if (isValid) {
        error = null
        isErrorEnabled = false
    } else {
        isErrorEnabled = true
        error = this.context.getString(errorStringId)
    }
}

fun EditText.onDoneTap(callback: () -> Unit) = onKeyTap(EditorInfo.IME_ACTION_DONE, callback)

private fun EditText.onKeyTap(imeKeyCode: Int, callback: () -> Unit) {
    imeOptions = imeKeyCode
    setOnEditorActionListener { _, actionId, _ ->
        return@setOnEditorActionListener if (actionId == imeKeyCode) {
            callback(); true
        } else false
    }
}


fun TextView.setDrawableStart(@DrawableRes icon: Int) {
    val drawable = AppCompatResources.getDrawable(this.context, icon)
    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setDrawableTop(@DrawableRes icon: Int) {
    val drawable = AppCompatResources.getDrawable(this.context, icon)
    setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
}

fun TextView.setDrawableEnd(@DrawableRes icon: Int) {
    val drawable = AppCompatResources.getDrawable(this.context, icon)
    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun TextView.setDrawableBottom(@DrawableRes icon: Int) {
    val drawable = AppCompatResources.getDrawable(this.context, icon)
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
}

fun ViewGroup.inflate(layoutRes: Int) = LayoutInflater.from(context).inflate(layoutRes, this, false)

fun EditText.hideKeyBord() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(
        this.windowToken, 0
    )
}

//fun View.hideAnimWithScale() {
//  if (this.alpha == 1f) {
//    this.animate()
//        .alpha(0f)
//        .scaleX(0f)
//        .scaleY(0f)
//        .setDuration(200)
//        .start()
//  }
//}
//
//fun View.showAnimWithScale() {
//  if (this.alpha == 0f) {
//    this.animate()
//        .alpha(1f)
//        .scaleY(1f)
//        .scaleX(1f)
//        .setDuration(200)
//        .start()
//  }
//}

fun View.hideInvisibleAnimWithScale(duration: Long = 200, interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
    if (this.isVisible()) {
        this.animate()
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .withEndAction { this.invisible() }
            .start()
    }
}

fun View.hideAnimWithScale(duration: Long = 200, interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
    if (this.isVisible()) {
        this.animate()
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .withEndAction { this.hide() }
            .start()
    }
}

fun View.showAnimWithScale(duration: Long = 200, interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
    if (this.isNotVisible()) {
        doOnPreDraw {
            alpha = 0f
            scaleY = 0f
            scaleX = 0f
            this.show()
            this.animate()
                .alpha(1f)
                .scaleY(1f)
                .scaleX(1f)
                .setInterpolator(interpolator)
                .setDuration(duration)
                .start()
        }
    }
}

fun View.showAnimWithScaleIf(show: Boolean, duration: Long = 200, interpolator: Boolean = false) {
    if (show) showAnimWithScale(
        duration,
        if (interpolator) OvershootInterpolator() else AccelerateDecelerateInterpolator()
    )
    else hideAnimWithScale(duration, if (interpolator) AnticipateInterpolator() else AccelerateDecelerateInterpolator())
}

fun View.showAnimWithReveal(duration: Long = 200, endAction: () -> Unit = {}, x: Int = -1, y: Int = -1) {
    if (isNotVisible()) {
        this.doOnPreDraw {
            alpha = 1f
            translationY = 0f
            translationX = 0f
            val centerX = if (x >= 0) x else width / 2
            val centerY = if (y >= 0) y else height / 2
            val finalRadius = Math.hypot(centerX.toDouble(), centerY.toDouble())

            val revealAnim = ViewAnimationUtils.createCircularReveal(this, centerX, centerY, 0f, finalRadius.toFloat())
            revealAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    endAction()
                }
            })

            show()
            revealAnim.start()
        }
    } else {
        endAction()
    }
}

fun View.hideAnimWithReveal(duration: Long = 200, endAction: () -> Unit = {}, x: Int = -1, y: Int = -1) {
    if (isVisible()) {
        val cx = if (x >= 0) x else width / 2
        val cy = if (y >= 0) y else height / 2
        val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble())

        val revealAnim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius.toFloat(), 0f)
        revealAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                hide()
                endAction()
            }
        })

        revealAnim.start()
    } else {
        endAction()
    }
}

fun View.blink(action: (() -> Unit)? = null) {

    this.alpha = 1f
    this.animate()
        .alpha(0f)
        .setDuration(200)
        .withEndAction {
            action?.invoke()
            this.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }
        .start()

}

fun View.showAnimWithSlideUp(duration: Long = 300, endAction: () -> Unit = {}) {
    if (isNotVisible()) {
        show()
        this.doOnPreDraw {
            translationY = height.toFloat()
            alpha = 0.5f
            animate()
                .alpha(1f)
                .translationY(0f)
                .withEndAction(endAction)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(duration)
                .start()
        }
    } else {
        endAction()
    }
}

fun View.hideAnimWithSlideDown(duration: Long = 300, endAction: () -> Unit = {}) {
    if (isVisible()) {
        animate()
            .alpha(0.5f)
            .translationY(height.toFloat())
            .withEndAction {
                hide()
                endAction()
            }
            .setInterpolator(AccelerateInterpolator())
            .setDuration(duration)
            .start()
    } else {
        endAction()
    }
}

fun View.invisibleAnimWithSlideDown(duration: Long = 300, endAction: () -> Unit = {}) {
    if (isVisible()) {
        animate()
            .alpha(0.5f)
            .translationY(height.toFloat())
            .withEndAction {
                invisible()
                endAction()
            }
            .setInterpolator(AccelerateInterpolator())
            .setDuration(duration)
            .start()
    } else {
        endAction()
    }
}


/**
 * Updates this view's padding. This version of the method allows using named parameters
 * to just set one or more axes.
 *
 * @see View.setPadding
 */
fun View.updatePadding(
    @Px left: Int = paddingLeft,
    @Px top: Int = paddingTop,
    @Px right: Int = paddingRight,
    @Px bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

/**
 * Executes [block] with the View's layoutParams and reassigns the layoutParams with the
 * updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
    updateLayoutParams<ViewGroup.LayoutParams>(block)
}

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
@JvmName("updateLayoutParamsTyped")
inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(block: T.() -> Unit) {
    val params = layoutParams as T
    block(params)
    layoutParams = params
}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition(), itemViewType)
    }
    return this
}


fun View.animateWeightChange(
    from: Int,
    to: Int,
    duration: Long = 600,
    startDelay: Long = 100,
    init: (ValueAnimator.() -> Unit)? = null,
    onValueChange: (Float) -> Unit
) {
    val valueAnimator =
        ValueAnimator.ofFloat(from.toFloat(), to.toFloat())
    valueAnimator.duration = duration
    valueAnimator.startDelay = startDelay
    valueAnimator.interpolator = FastOutSlowInInterpolator()
    init?.let { valueAnimator.it() }
    valueAnimator.addUpdateListener {
        this.updateLayoutParams<LinearLayout.LayoutParams> {
            weight = it.animatedValue as Float
        }
        onValueChange.invoke(it.animatedValue as Float)
        this.requestLayout()
    }
    valueAnimator.start()
}

fun TextView.countTo(to: Int,
                     sign: String = "",
                     duration: Long = 1000) {
    val old = Integer.parseInt(text.toString().replace(sign, ""))
    val vAnimator = ValueAnimator.ofInt(old, to)
    vAnimator.duration = duration
    vAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        text = "${value}${sign}"
    }
    vAnimator.start()
}