package com.example.kidsmarblejar.presentation.home

data class AddUserModel(
    override val id: Int = -1,
    override val name: String = "Add new user",
    override val image: ByteArray? = null,
    override val isParent: Boolean = false,
    override val onClick: () -> Unit
): UserModel

data class ParentUserModel(
    override val id: Int,
    override val name: String,
    override val image: ByteArray?,
    override val isParent: Boolean,
    override val onClick: () -> Unit
) : UserModel

data class ChildUserModel(
    override val id: Int,
    override val name: String,
    override val image: ByteArray?,
    override val isParent: Boolean,
    val marbles: Int,
    val conversionType: Int,
    val conversionValue: Int,
    val requiresPassword: Boolean,
    val goal: Int,
    val goalName: String,
    override val onClick: () -> Unit
) : UserModel

interface UserModel{
    val id: Int
    val name: String
    val image: ByteArray?
    val isParent: Boolean
    val onClick: () -> Unit
}