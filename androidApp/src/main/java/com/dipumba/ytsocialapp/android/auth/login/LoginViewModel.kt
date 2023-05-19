package com.dipumba.ytsocialapp.android.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dipumba.ytsocialapp.android.common.datastore.UserPreferences
import com.dipumba.ytsocialapp.auth.domain.usecase.SignInUseCase
import com.dipumba.ytsocialapp.common.util.Result
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCase,
    private val dataStore: DataStore<UserPreferences>
): ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun signIn(){
        viewModelScope.launch {
            uiState = uiState.copy(isAuthenticating = true)

            val authResultData = signInUseCase(uiState.email, uiState.password)

            uiState = when(authResultData){
                is Result.Error -> {
                    uiState.copy(
                        isAuthenticating = false,
                        authErrorMessage = authResultData.message
                    )
                }
                is Result.Success -> {
                    dataStore.updateData {
                        it.copy(
                            id = authResultData.data!!.id,
                            name = authResultData.data!!.name,
                            bio = authResultData.data!!.bio,
                            avatar = authResultData.data!!.avatar,
                            token = authResultData.data!!.token,
                            followersCount = authResultData.data!!.followersCount,
                            followingCount = authResultData.data!!.followingCount,
                        )
                    }
                    uiState.copy(
                        isAuthenticating = false,
                        authenticationSucceed = true
                    )
                }
            }
        }
    }

    fun updateEmail(input: String){
        uiState = uiState.copy(email = input)
    }

    fun updatePassword(input: String){
        uiState = uiState.copy(password = input)
    }
}

data class LoginUiState(
    var email: String = "",
    var password: String = "",
    var isAuthenticating: Boolean = false,
    var authErrorMessage: String? = null,
    var authenticationSucceed: Boolean = false
)













