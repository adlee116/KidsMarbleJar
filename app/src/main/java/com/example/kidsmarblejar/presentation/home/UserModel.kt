package com.example.kidsmarblejar.presentation.home

data class AddUserModel(
    override var id: Int = -1,
    override var name: String = "Add new user",
    override var image: String = "",
    override var isParent: Boolean = false,
    override var onClick: () -> Unit
): UserModel

data class ParentUserModel(
    override val id: Int,
    override val name: String,
    override val image: String,
    override val isParent: Boolean,
    override val onClick: () -> Unit
) : UserModel

data class ChildUserModel(
    override val id: Int,
    override val name: String,
    override val image: String,
    override val isParent: Boolean,
    val marbles: Int,
    val conversionType: Int,
    val conversionValue: Double,
    val requiresPassword: Boolean,
    val goal: Int,
    val goalName: String,
    override val onClick: () -> Unit
) : UserModel

interface UserModel{
    val id: Int
    val name: String
    val image: String
    val isParent: Boolean
    val onClick: () -> Unit
}