package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.api.Tokens
import com.rohitthebest.phoco_theimagesearchingapp.api.UserResponse
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.PhocoRepository
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.PhocoUser
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.SignUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PhocoViewModel @Inject constructor(
        private val repository: PhocoRepository
) : ViewModel() {

    private val _phocoUserResponseSignUp = MutableLiveData<Resources<UserResponse>>()
    private val _phocoTokenResponse = MutableLiveData<Resources<Tokens>>()
    private val _phocoPhocoUserResponse = MutableLiveData<Resources<PhocoUser>>()

    val phocoUserResponseSignUp: LiveData<Resources<UserResponse>> get() = _phocoUserResponseSignUp
    val phocoTokenResponse: LiveData<Resources<Tokens>> get() = _phocoTokenResponse
    val phocoPhocoUserResponse: LiveData<Resources<PhocoUser>> get() = _phocoPhocoUserResponse

    fun signUpUser(signUp: SignUp) {

        try {

            viewModelScope.launch {

                _phocoUserResponseSignUp.postValue(Resources.Loading())

                repository.signUp(signUp).let {

                    if (it.isSuccessful) {

                        _phocoUserResponseSignUp.postValue(Resources.Success(
                                it.body(),
                                it.code().toString()
                        ))
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

                repository.loin(username, password).let {

                    if (it.isSuccessful) {

                        _phocoTokenResponse.postValue(Resources.Success(
                                it.body(),
                                it.code().toString()
                        ))
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

                repository.getNewTokens(refreshToken).let {

                    if (it.isSuccessful) {

                        _phocoTokenResponse.postValue(Resources.Success(
                                it.body(),
                                it.code().toString()
                        ))
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

                    repository.getPhocoUserByPrimaryKey(primaryKey = primaryKey, accessToken).let {

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

            _phocoPhocoUserResponse.postValue(Resources.Success(
                    response.body(),
                    response.code().toString()
            ))
        } else {

            _phocoPhocoUserResponse.postValue(Resources.Error(response.message()))
        }

    }
}