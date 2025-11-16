package com.tendertool.app.src

import android.util.Log
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import com.tendertool.app.db.TenderDao
import com.tendertool.app.models.BaseTender
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TenderRepository(
    private val tenderDao: TenderDao,
    private val apiService: APIService
) {

    val watchlistTenders: Flow<List<BaseTender>> = tenderDao.getWatchlistTenders()

    /**
     * Fetches the latest watchlist from the API and updates the local Room database.
     */
    suspend fun refreshWatchlist() {
        Log.d("TenderRepository", "Refreshing watchlist...")
        try {
            // Get the user ID from Amplify
            val userID = getCoreID()

            // Fetch fresh data from your Retrofit API
            val apiResponse = apiService.getWatchlist(userID)
            val apiTenders = apiResponse.watchlist

            // Clear the old local data
            tenderDao.clearWatchlist()

            // Insert the new, fresh data into the local database
            tenderDao.insertAll(apiTenders)

            Log.i("TenderRepository", "Watchlist synced successfully. ${apiTenders.size} tenders.")

        } catch (e: Exception) {
            // If the network fails, we don't crash.
            // The user will just keep seeing the old data from Room.
            Log.e("TenderRepository", "Failed to sync watchlist", e)
            throw e
        }
    }

    /**
     * Toggles a tender's watchlist status and then refreshes the entire list.
     */
    suspend fun toggleWatchlist(tenderID: String) {
        Log.d("TenderRepository", "Toggling watch for: $tenderID")
        try {
            val userID = getCoreID()
            val authToken = getAuthToken()

            // Call the toggle API
            apiService.toggleWatchlist("Bearer $authToken", userID, tenderID)

            // After toggling, refresh the entire watchlist
            // This ensures our local database is perfectly in sync.
            refreshWatchlist()

        } catch (e: Exception) {
            Log.e("TenderRepository", "Failed to toggle watchlist", e)
            throw e
        }
    }

    // Amplify Helper Functions

    private suspend fun getCoreID(): String {
        return suspendCoroutine { continuation ->
            Amplify.Auth.fetchUserAttributes(
                { attributes ->
                    val coreID = attributes.firstOrNull { it.key.keyString == "custom:CoreID" }?.value
                    if (coreID != null) {
                        continuation.resume(coreID)
                    } else {
                        continuation.resumeWithException(Exception("CoreID not found."))
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                }
            )
        }
    }

    private suspend fun getAuthToken(): String {
        return suspendCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession(
                { session ->
                    val cognitoSession = session as? AWSCognitoAuthSession
                    val idToken = cognitoSession?.userPoolTokensResult?.value?.idToken
                    if (idToken != null) {
                        continuation.resume(idToken)
                    } else {
                        continuation.resumeWithException(Exception("Failed to get Cognito token"))
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                }
            )
        }
    }
}