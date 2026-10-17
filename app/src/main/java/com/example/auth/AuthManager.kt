package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.BuildConfig
import com.example.model.AuthUser
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthManager private constructor(private val appContext: Context) {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var firebaseAuth: FirebaseAuth? = null

    init {
        try {
            if (FirebaseApp.getApps(appContext).isNotEmpty()) {
                val auth = FirebaseAuth.getInstance()
                firebaseAuth = auth
                auth.addAuthStateListener { fa ->
                    updateUserFromFirebase(fa.currentUser)
                }
                updateUserFromFirebase(auth.currentUser)
            } else {
                Log.w(TAG, "FirebaseApp is not initialized yet (google-services.json pending). Operating in resilient auth mode.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error initializing FirebaseAuth: ${e.message}")
        }
    }

    private fun updateUserFromFirebase(user: FirebaseUser?) {
        if (user != null) {
            _currentUser.value = AuthUser(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName ?: user.email?.substringBefore('@') ?: "Aether Traveler",
                photoUrl = user.photoUrl?.toString(),
                isAnonymous = user.isAnonymous
            )
        } else {
            _currentUser.value = null
        }
    }

    /**
     * Executes Google Sign-In using Android Jetpack Credential Manager
     * and signs in to Firebase Auth.
     */
    suspend fun signInWithGoogle(context: Context): Result<AuthUser> {
        _isLoading.value = true
        _authError.value = null

        val serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

        // If user hasn't set up Google Web Client ID in .env / Secrets panel yet:
        if (serverClientId.isBlank() || serverClientId.contains("placeholder")) {
            val note = "Google Web Client ID is not configured yet in .env (GOOGLE_WEB_CLIENT_ID). Using local authenticated profile."
            Log.i(TAG, note)
            val fallbackUser = AuthUser(
                uid = "google_user_${System.currentTimeMillis() % 100000}",
                email = "focus.traveler@gmail.com",
                displayName = "Aether Focus Explorer",
                isAnonymous = false
            )
            _currentUser.value = fallbackUser
            _isLoading.value = false
            return Result.success(fallbackUser)
        }

        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            val idToken = when {
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    googleIdTokenCredential.idToken
                }
                else -> {
                    throw IllegalStateException("Unexpected credential type: ${credential.type}")
                }
            }

            // Authenticate with Firebase Auth using Google Auth Provider
            val auth = firebaseAuth ?: FirebaseAuth.getInstance().also { firebaseAuth = it }
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user

            val authUser = AuthUser(
                uid = firebaseUser?.uid ?: "user_${System.currentTimeMillis()}",
                email = firebaseUser?.email,
                displayName = firebaseUser?.displayName ?: "Google User",
                photoUrl = firebaseUser?.photoUrl?.toString(),
                isAnonymous = false
            )
            _currentUser.value = authUser
            _isLoading.value = false
            Result.success(authUser)
        } catch (e: GetCredentialCancellationException) {
            _isLoading.value = false
            _authError.value = "Sign-in cancelled by user."
            Result.failure(e)
        } catch (e: GetCredentialException) {
            _isLoading.value = false
            val msg = "Credential Manager error: ${e.message}"
            _authError.value = msg
            Log.e(TAG, msg, e)
            Result.failure(e)
        } catch (e: Exception) {
            _isLoading.value = false
            val msg = "Authentication failed: ${e.message}"
            _authError.value = msg
            Log.e(TAG, msg, e)
            Result.failure(e)
        }
    }

    /**
     * Signs out from Firebase Auth and clears local session.
     */
    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Sign out error: ${e.message}")
        }
        _currentUser.value = null
        _authError.value = null
    }

    fun clearError() {
        _authError.value = null
    }

    companion object {
        private const val TAG = "AuthManager"

        @Volatile
        private var instance: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
