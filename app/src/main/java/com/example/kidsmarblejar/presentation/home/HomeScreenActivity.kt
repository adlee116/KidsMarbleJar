package com.example.kidsmarblejar.presentation.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.kidsmarblejar.databinding.ActivityHomeScreenBinding
import com.example.kidsmarblejar.presentation.addUser.AddUserActivity
import com.example.kidsmarblejar.presentation.marbleJar.MarbleJarActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeScreenBinding
    private val viewModel: HomeViewModel by viewModel()

    private val adapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setAdapter()
        setClickListeners()
        observeViewModel()
        viewModel.process(HomeEvent.Initialise)
    }

    private fun setAdapter() {
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        binding.userRecycler.layoutManager = layoutManager
        binding.userRecycler.adapter = adapter
    }

    private fun setClickListeners() {

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.users.collectLatest {
                adapter.updateAllItems(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.userClicked.collectLatest {
                when {
                    it.first == -1 -> addUserClicked()
                    it.second -> parentUserClicked(it.first)
                    else -> childUserClicked(it.first)

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.userAdded.collectLatest {

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.homeState.collectLatest {
                binding.loadingPanel.isVisible = it is HomeState.Loading
            }
        }
    }

    private val startAddUserActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.process(HomeEvent.UserAdded)
        }
    }

    private fun addUserClicked() {
        startAddUserActivity.launch(Intent(this@HomeScreenActivity, AddUserActivity::class.java))
    }

    private fun parentUserClicked(id: Int) {
        startAddUserActivity.launch(Intent(this@HomeScreenActivity, AddUserActivity::class.java).apply {
            putExtra(AddUserActivity.USER, id)
        })
    }

    private fun childUserClicked(id: Int) {
        MarbleJarActivity.start(this, id)
    }
}

