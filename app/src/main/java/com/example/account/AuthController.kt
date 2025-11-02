package com.example.account


import com.example.account.BuildConfig.SUPABASE_KEY
import com.example.account.BuildConfig.SUPABASE_URL
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.Github
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

object AuthController {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        println("SUPABASE_URL: ${BuildConfig.SUPABASE_URL}")
        println("SUPABASE_KEY: ${BuildConfig.SUPABASE_KEY}")
    }

    private val supabase = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // Sign up new user
    fun signUp(
        firstname: String,
        lastname: String,
        email: String,
        password: String,
        phonenumber: String
    ) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                _authState.value = AuthState.Loading

                // Create auth user
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                //Get the user ID from currentUserOrNull
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("User ID not found")

                //Insert user profile into your table
                supabase.from("Account").insert(
                    mapOf(
                        "id" to userId,
                        "firstname" to firstname,
                        "lastname" to lastname,
                        "email" to email,
                        "phonenumber" to phonenumber
                    )
                )

                _authState.value = AuthState.Success("Account created successfully!")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    // Sign in existing user
    fun signIn(email: String, password: String) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                _authState.value = AuthState.Loading

                // Step 1: Sign in with Supabase Auth
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Failed to get user ID")

                //Fetch user profile from your table
                val userProfile = supabase.from("Account")
                    .select() {
                        filter {
                            eq("id", userId)
                        }
                    }.decodeSingle<Entity>()

                _authState.value = AuthState.Success("Login successful!")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    // Sign in with GitHub
    fun signInWithGitHub(onLaunchUrl: (String) -> Unit) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                _authState.value = AuthState.Loading

                // Start GitHub OAuth flow
                supabase.auth.signInWith(Github) {
                    // This will generate an OAuth URL
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "GitHub login failed")
            }
        }
    }

    // Handle OAuth callback and create/update user profile
    suspend fun handleOAuthCallback() = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: throw Exception("Failed to get user ID")

            val userMetadata = supabase.auth.currentUserOrNull()?.userMetadata
            val email = supabase.auth.currentUserOrNull()?.email ?: ""

            // Extract name from GitHub metadata
            val fullName = userMetadata?.get("full_name")?.toString() ?: ""
            val nameParts = fullName.split(" ", limit = 2)
            val firstname = nameParts.getOrNull(0) ?: ""
            val lastname = nameParts.getOrNull(1) ?: ""

            // Check if user profile already exists
            val existingProfile = try {
                supabase.from("Account")
                    .select() {
                        filter {
                            eq("id", userId)
                        }
                    }.decodeSingle<Entity>()
            } catch (e: Exception) {
                null
            }

            // If profile doesn't exist, create it
            if (existingProfile == null) {
                supabase.from("Account").insert(
                    mapOf(
                        "id" to userId,
                        "firstname" to firstname,
                        "lastname" to lastname,
                        "email" to email,
                        "phonenumber" to ""
                    )
                )
            }

            _authState.value = AuthState.Success("Login successful!")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Profile creation failed")
        }
    }

    // Sign out
    fun logout(onComplete: () -> Unit = {}) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState.Idle
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Logout failed")
            }
        }
    }

    // Get current logged-in user
    suspend fun getCurrentUser(): Entity? = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@withContext null

            supabase.from("Account")
                .select() {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingle<Entity>()
        } catch (e: Exception) {
            null
        }
    }
}