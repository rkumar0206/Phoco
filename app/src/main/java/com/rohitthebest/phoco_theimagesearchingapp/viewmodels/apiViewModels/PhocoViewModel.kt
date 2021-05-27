package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

private const val TAG = "PhocoViewModel"

@HiltViewModel
class PhocoViewModel @Inject constructor(
    private val repository: PhocoRepository
) : ViewModel() {

    //authentication and user related vars
    private val _phocoUserResponseSignUp = MutableLiveData<Resources<UserResponse>>()
    private val _phocoTokenResponse = MutableLiveData<Resources<Tokens>>()
    private val _phocoPhocoUserResponse = MutableLiveData<Resources<PhocoUser>>()

    val phocoUserResponseSignUp: LiveData<Resources<UserResponse>> get() = _phocoUserResponseSignUp
    val phocoTokenResponse: LiveData<Resources<Tokens>> get() = _phocoTokenResponse
    val phocoPhocoUserResponse: LiveData<Resources<PhocoUser>> get() = _phocoPhocoUserResponse

    // follow related vars
    private val _followersList = MutableLiveData<Resources<List<PhocoUser>>>()
    private val _followingList = MutableLiveData<Resources<List<PhocoUser>>>()
    private val _followUser = MutableLiveData<Resources<Follow>>()
    private val _unfollowUser = MutableLiveData<Resources<String?>>()

    val followersList: LiveData<Resources<List<PhocoUser>>> get() = _followersList
    val followingList: LiveData<Resources<List<PhocoUser>>> get() = _followingList
    val followUser: LiveData<Resources<Follow>> get() = _followUser
    val unfollowUser: LiveData<Resources<String?>> get() = _unfollowUser

    // user's image related vars
    private val _imageList = MutableLiveData<Resources<List<PhocoImageItem>>>()
    private val _uploadImage = MutableLiveData<Resources<PhocoImageItem>>()

    val imageList: LiveData<Resources<List<PhocoImageItem>>> get() = _imageList
    val uploadImage: LiveData<Resources<PhocoImageItem>> get() = _uploadImage

    /*start function*/

    //user authentication related
    fun signUpUser(signUp: SignUp) {

        try {

            viewModelScope.launch {

                _phocoUserResponseSignUp.postValue(Resources.Loading())

                repository.signUp(signUp).also {

                    if (it.isSuccessful) {

                        _phocoUserResponseSignUp.postValue(
                            Resources.Success(
                                it.body(),
                                it.code().toString()
                            )
                        )
                    } else {

                        _phocoUserResponseSignUp.postValue(Resources.Error(it.message()))
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun loginUser(username: String, password: String) {

        try {

            viewModelScope.launch {

                _phocoTokenResponse.postValue(Resources.Loading())

                repository.loin(username, password).also {

                    if (it.isSuccessful) {

                        _phocoTokenResponse.postValue(
                            Resources.Success(
                                it.body(),
                                it.code().toString()
                            )
                        )
                    } else {

                        _phocoTokenResponse.postValue(Resources.Error(it.message()))
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getNewTokens(refreshToken: String) {

        try {

            viewModelScope.launch {

                _phocoTokenResponse.postValue(Resources.Loading())

                repository.getNewTokens(refreshToken).also {

                    if (it.isSuccessful) {

                        _phocoTokenResponse.postValue(
                            Resources.Success(
                                it.body(),
                                it.code().toString()
                            )
                        )
                    } else {

                        _phocoTokenResponse.postValue(Resources.Error(it.code().toString()))
                    }
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    fun getPhocoUser(primaryKey: Int? = null, username: String? = null, accessToken: String) {

        try {

            viewModelScope.launch {

                _phocoPhocoUserResponse.postValue(Resources.Loading())

                if (primaryKey != null) {

                    repository.getPhocoUserByPrimaryKey(primaryKey = primaryKey, accessToken).also {

                        handlePhocoUserValue(it)
                    }

                } else {

                    username?.let {

                        repository.getPhocoUserByUsername(it, accessToken).let { response ->

                            handlePhocoUserValue(response)
                        }
                    }
                }

            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun handlePhocoUserValue(response: Response<PhocoUser>) {

        if (response.isSuccessful) {

            _phocoPhocoUserResponse.postValue(
                Resources.Success(
                    response.body(),
                    response.code().toString()
                )
            )
        } else {

            _phocoPhocoUserResponse.postValue(Resources.Error(response.message()))
        }

    }

    // user follow and following related
    fun getFollowersListOfUser(accessToken: String, userPrimaryKey: Int) {

        try {

            viewModelScope.launch {

                _followersList.postValue(Resources.Loading())

                repository.getUserFollowers(accessToken, userPrimaryKey).also {

                    if (it.isSuccessful) {

                        _followersList.postValue(Resources.Success(it.body(), it.code().toString()))
                    } else {

                        _followersList.postValue(Resources.Error(it.message()))
                    }
                }

            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    fun getFollowingListOfUser(accessToken: String, userPrimaryKey: Int) {

        try {

            viewModelScope.launch {

                _followingList.postValue(Resources.Loading())

                repository.getUserFollowing(accessToken, userPrimaryKey).also {

                    if (it.isSuccessful) {

                        _followingList.postValue(Resources.Success(it.body(), it.code().toString()))
                    } else {

                        _followingList.postValue(Resources.Error(it.message()))
                    }
                }

            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
    fun followTheUser(accessToken: String, follower_user_pk: Int, following_user_pk: Int) {

        try {

            viewModelScope.launch {

                _followUser.postValue(Resources.Loading())

                repository.followUser(accessToken, follower_user_pk, following_user_pk).also {

                    if (it.isSuccessful) {

                        _followUser.postValue(Resources.Success(it.body(), it.code().toString()))
                    } else {

                        _followUser.postValue(Resources.Error(it.message()))
                    }
                }

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
    fun unfollowTheUser(accessToken: String, follower_user_pk: Int, following_user_pk: Int) {

        try {

            viewModelScope.launch {

                _unfollowUser.postValue(Resources.Loading())

                repository.unfollowUser(accessToken, follower_user_pk, following_user_pk).also {

                    if (it.isSuccessful) {

                        _unfollowUser.postValue(Resources.Success("", it.code().toString()))
                    } else {

                        _unfollowUser.postValue(Resources.Error(it.message()))
                    }
                }

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    //user's image related
    fun getImageList(accessToken: String, username: String) {

        try {

            viewModelScope.launch {

                repository.getUserPhocoImages(accessToken, username).also {

                    _imageList.postValue(Resources.Loading())

                    if (it.isSuccessful) {

                        _imageList.postValue(Resources.Success(it.body(), it.code().toString()))
                    } else {

                        _imageList.postValue(Resources.Error(it.message()))
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _imageList.postValue(Resources.Error("Something went wrong"))
        }

    }

    fun uploadImage(
        accessToken: String,
        imageName: String,
        /*fileRequestBody: RequestBody,*/
        file: File,
        imageDescription: String = "",
        userPK: String
    ) {

        try {

            viewModelScope.launch {

                Log.d(TAG, "uploadImage: started")

                _uploadImage.postValue(Resources.Loading())

                // without progress update
                val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())

                val imageMultipart = MultipartBody.Part.createFormData(
                    "image",  // field name as in API
                    imageName,
                    requestImageFile
                )

                Log.d(TAG, "uploadImage : ImageMultipart : $imageMultipart")

                val imageDescriptionRequestBody =
                    imageDescription.toRequestBody("text/plain".toMediaTypeOrNull())
                Log.d(
                    TAG,
                    "uploadImage : imageDescriptionRequestBody : $imageDescriptionRequestBody"
                )

                val userPkRequestBody = userPK.toRequestBody("text/plain".toMediaTypeOrNull())
                Log.d(TAG, "uploadImage : userPkRequestBody : $userPkRequestBody")

                repository.postImage(
                    accessToken,
                    imageMultipart,
                    imageDescriptionRequestBody,
                    userPkRequestBody
                ).also {

                    if (it.isSuccessful) {

                        Log.d(TAG, "uploadImage: Success : ${it.code()} ${it.message()}")
                        _uploadImage.postValue(Resources.Success(it.body(), it.code().toString()))
                    } else {

                        Log.d(TAG, "uploadImage: Error : ${it.code()} ${it.message()}")
                        _uploadImage.postValue(
                            Resources.Error(
                                it.code().toString() + " " + it.message()
                            )
                        )
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /*end functions*/
}