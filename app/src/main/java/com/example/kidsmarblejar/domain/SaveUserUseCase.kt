package com.example.kidsmarblejar.domain

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.example.kidsmarblejar.core.BaseUseCase
import com.example.kidsmarblejar.core.Result
import com.example.kidsmarblejar.data.UsersRepository
import com.example.kidsmarblejar.presentation.addUser.RewardType
import com.example.kidsmarblejar.presentation.home.UserEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class SaveUserUseCase(private val usersRepository: UsersRepository): BaseUseCase<Int, NewUser>() {
    override suspend fun run(params: NewUser): Result<Int, Exception> {
        return try {
            val newUser = createNewUser(params)
            Result.Success(usersRepository.addUser(newUser))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    private fun createNewUser(newUser: NewUser): UserEntity {
        return UserEntity(
            name = newUser.name,
            image = newUser.image,
            marbles = 0,
            conversionType = newUser.rewardType.rewardType,
            conversionValue = newUser.marbleConversionRate,
            requiresPassword = newUser.requiresPassword,
            goal = newUser.marblesRequired,
            goalName = newUser.rewardName,
            isAdult = newUser.isAdult
        )
    }

    private fun getImageUri(inImage: Bitmap?): Uri? {
        if(inImage == null) return null
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.absolutePath + "/.temp/")
        tempDir.mkdir()
        val tempFile = File.createTempFile(UUID.randomUUID().toString().substring(0,5) + "userImage", ".jpg", tempDir)
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData: ByteArray = bytes.toByteArray()

        //write the bytes in file

        //write the bytes in file
        val fos = FileOutputStream(tempFile)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return Uri.fromFile(tempFile)
    }
}

data class NewUser(
    var name: String = "",
    var image: String = "",
    var requiresPassword: Boolean = false,
    var isAdult: Boolean = false,
    var rewardType: RewardType = RewardType.REWARD,
    var marbleConversionRate: Double = -1.0,
    var rewardName: String = "",
    var marblesRequired: Int = -1
)