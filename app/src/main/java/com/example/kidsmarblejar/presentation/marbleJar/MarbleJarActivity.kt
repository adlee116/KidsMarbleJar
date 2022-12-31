package com.example.kidsmarblejar.presentation.marbleJar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.kidsmarblejar.databinding.ActivityMarbleJarBinding
import com.example.kidsmarblejar.presentation.addUser.AddUserActivity
import com.example.kidsmarblejar.presentation.home.UserEntity
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarbleJarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarbleJarBinding
    private val adapter = MarbleJarAdapter()
    private val viewModel: MarbleJarViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarbleJarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()
        initObservers()
        setClickListeners()
        getData()
    }

    private fun setClickListeners() {
        binding.addMarbleButton.setOnClickListener {
            viewModel.process(MarbleJarEvent.AddMarbles)
        }
        binding.profileImage.setOnClickListener {
            startAddUserActivity.launch(
                Intent(this@MarbleJarActivity, AddUserActivity::class.java).apply {
                    putExtra(AddUserActivity.USER, viewModel.userEntity.id)
                }
            )
        }
    }

    private val startAddUserActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.process(MarbleJarEvent.Initialise(viewModel.userEntity.id))
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.marbleJarState.collectLatest {
                setLoading(it == MarbleJarState.Loading)
                when (it) {
                    is MarbleJarState.Reading -> initView(it.user, it.marbles)
                    else -> {}
                }
            }
        }
    }

    private fun initView(user: UserEntity, marbles: List<Marble>) {
        val name = user.name
        if(name.isEmpty()) {
            Toast.makeText(this, "Users name was null or empty", Toast.LENGTH_SHORT).show()
            finish()
        }
        val plural = if(name.last() == 's') {
            "$name'"
        } else {
            "$name's"
        }
        binding.title.text = "$plural Marbles"
        adapter.updateAllItems(marbles)
        Glide.with(binding.root.context)
            .load(user.image)
            .fitCenter()
            .into(binding.profileImage)
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingPanel.isVisible = loading
    }

    private fun getData() {
        val id = intent.getIntExtra(USER_ID, -1)
        if (id == -1) {
            Toast.makeText(this, "Unable to get user", Toast.LENGTH_SHORT).show()
            finish()
        }
        viewModel.process(MarbleJarEvent.Initialise(id))
    }

    private fun setAdapter() {
        val layoutManager = FlexboxLayoutManager(this)
        binding.marbleAdapter.layoutManager = layoutManager
        binding.marbleAdapter.adapter = adapter
    }

    companion object {
        const val USER_ID = "userId"

        fun start(context: Context, userId: Int) = context.startActivity(
            Intent(context, MarbleJarActivity::class.java).apply {
                putExtra(USER_ID, userId)
            }
        )

    }
}