package com.rafalskrzypczyk.auth.data

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.rafalskrzypczyk.auth.BuildConfig
import com.rafalskrzypczyk.auth.domain.GoogleCredentialsProvider
import javax.inject.Inject

class GoogleCredentialsProviderImpl @Inject constructor() : GoogleCredentialsProvider {

    override suspend fun getGoogleIdToken(context: Context): String? {
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        )
            .setNonce("")
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val credential = CredentialManager.create(context)
            .getCredential(request = request, context = context)
            .credential

        if (credential !is CustomCredential) return null
        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return null

        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    override suspend fun clearCredentialState(context: Context) {
        CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
    }
}
