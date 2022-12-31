package com.example.kidsmarblejar.presentation.addUser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.kidsmarblejar.R
import com.example.kidsmarblejar.databinding.ActivityNewUserBinding
import com.example.kidsmarblejar.databinding.CameraBottomDialogBinding
import com.example.kidsmarblejar.domain.NewUser
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewUserBinding
    private val viewModel: AddUserViewModel by viewModel()
    private var isBottomSheetOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setClickListeners()
        observeViewModel()
        getDataIfRequired()
        viewModel.process(AddUserEvent.Initialise)
    }

    private fun getDataIfRequired() {
        if (intent.hasExtra(USER)) {
            getData()
        } else {
            viewModel.process(AddUserEvent.Initialise)
        }
    }

    private fun getData() {
        val userEntity = intent.getIntExtra(USER, -1)
        if (userEntity != -1) {
            viewModel.process(AddUserEvent.GetUser(userEntity))
        }
    }

    private fun closeClickListeners() {
        binding.addImageButton.setOnClickListener(null)
        binding.addImageButton.setOnClickListener(null)
        binding.requiresPassword.setOnCheckedChangeListener(null)
        binding.adultToggleButton.setOnCheckedChangeListener(null)
        binding.moneyToggleButton.setOnClickListener(null)
        binding.rewardToggleButton.setOnClickListener(null)
        binding.saveButton.setOnClickListener(null)
    }

    private fun setClickListeners() {
        binding.addImageButton.setOnClickListener { openBottomDialogForCamera() }
        binding.requiresPassword.setOnCheckedChangeListener { _, isChecked ->
            viewModel.process(AddUserEvent.RequiresPasswordToggled(isChecked))
        }
        binding.adultToggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.process(AddUserEvent.ParentToggled(isChecked))
        }
        binding.moneyToggleButton.setOnClickListener {
            viewModel.process(AddUserEvent.MoneyToggled)
        }
        binding.rewardToggleButton.setOnClickListener {
            viewModel.process(AddUserEvent.RewardToggled)
        }
        binding.saveButton.setOnClickListener {
            when {
                viewModel.newUser.isAdult -> viewModel.process(AddUserEvent.SaveAdultClicked(binding.firstNameTextFieldEditText.text.toString().trim()))
                viewModel.newUser.rewardType == RewardType.REWARD -> viewModel.process(
                    AddUserEvent.SaveRewardClicked(
                        binding.firstNameTextFieldEditText.text.toString().trim(),
                        binding.rewardNameEditText.text.toString().trim(),
                        binding.marblesRequiredEditText.text.toString().trim().toInt()
                    )
                )
                else -> viewModel.process(
                    AddUserEvent.SaveMoneyClicked(
                        binding.firstNameTextFieldEditText.text.toString().trim(),
                        binding.marbleValueEditText.text.toString().trim().toDouble()
                    )
                )
            }
        }
    }

    private val startCameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra(CameraActivity.RESULT)
            uriString?.let { setImage(it) }
        }
    }

    private fun setImage(uriString: String) {
        val uri = uriString.toUri()
        uri.let {
            Glide.with(binding.root.context)
                .load(it)
                .placeholder(R.drawable.add_user_image)
                .fitCenter()
                .into(binding.addImageButton)
            viewModel.process(AddUserEvent.AddedImage(it.toString()))
        }
    }

    private fun openBottomDialogForCamera() {
        if (isBottomSheetOpen) return
        BottomSheetDialog(this).apply {
            val binding = CameraBottomDialogBinding.inflate(layoutInflater, null, false)
            setContentView(binding.root)
            binding.photoButton.setOnClickListener {
                dismiss()
                startCameraActivity.launch(Intent(this@AddUserActivity, CameraActivity::class.java))
            }

            binding.galleryButton.setOnClickListener {
                dismiss()
            }

            setOnDismissListener {
                isBottomSheetOpen = false
            }
        }.show()

    }

    private fun initView(addUserState: AddUserState) {
        when (addUserState) {
            AddUserState.Loading -> binding.loadingPanel.isVisible = true
            is AddUserState.Reading -> {
                binding.loadingPanel.isVisible = false
                updateUi(addUserState.newUser)
            }
        }
    }

    private fun updateUi(newUser: NewUser) {
        closeClickListeners()
        binding.moneyToggleButton.isVisible = !newUser.isAdult
        binding.rewardToggleButton.isVisible = !newUser.isAdult
        binding.requiresPassword.isVisible = !newUser.isAdult
        binding.marbleValueLayout.isVisible = !newUser.isAdult
        binding.rewardNameLayout.isVisible = !newUser.isAdult
        binding.marblesRequiredLayout.isVisible = !newUser.isAdult
        binding.valueInfoIcon.isVisible = !newUser.isAdult

        if (!newUser.isAdult) {
            binding.moneyToggleButton.isSelected = newUser.rewardType == RewardType.MONEY
            binding.rewardToggleButton.isSelected = newUser.rewardType == RewardType.REWARD
            binding.requiresPassword.isSelected = newUser.requiresPassword
            binding.adultToggleButton.isSelected = newUser.isAdult
            binding.marbleValueLayout.isVisible = newUser.rewardType == RewardType.MONEY
            binding.rewardNameLayout.isVisible = newUser.rewardType == RewardType.REWARD
            binding.marblesRequiredLayout.isVisible = newUser.rewardType == RewardType.REWARD
        }

        binding.firstNameTextFieldEditText.setText(newUser.name)
        if (newUser.image.isNotEmpty()) {
            Glide.with(this)
                .load(newUser.image)
                .centerCrop()
                .into(binding.addImageButton)
        }
        if (binding.rewardNameLayout.isVisible) binding.rewardNameEditText.setText(newUser.rewardName)
        if (binding.marblesRequiredLayout.isVisible && newUser.marblesRequired > 0) binding.marblesRequiredEditText.setText(newUser.marblesRequired.toString())
        if (binding.marbleValueLayout.isVisible) binding.marbleValueEditText.setText(newUser.marbleConversionRate.toString())
        setClickListeners()
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        setResult(RESULT_CANCELED, resultIntent)
        finish()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.addUserState.collectLatest {
                initView(it)
            }
        }
        lifecycleScope.launch {
            viewModel.saveFailed.collectLatest {
                Toast.makeText(
                    this@AddUserActivity,
                    "Unexpected error, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        lifecycleScope.launch {
            viewModel.saved.collectLatest {
                val resultIntent = Intent()
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
        lifecycleScope.launch {
            viewModel.getUserFailed.collectLatest {
                val resultIntent = Intent()
                setResult(RESULT_CANCELED, resultIntent)
                finish()
            }
        }
    }

    companion object {

        const val USER = "user"

    }

}