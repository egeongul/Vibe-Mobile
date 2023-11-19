package viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cs308_00.R
import model.RequestDataInterface
import network.APIRequest
import network.RetrofitClient
import network.constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import view.Dashboard
import view.LoginPage
import view.ui.main.IncomingRequests

class IncomingRequestsViewModel: ViewModel() {

    private lateinit var ctx: IncomingRequests
    var invitationList: List<RequestDataInterface.friendRequests> = emptyList<RequestDataInterface.friendRequests>()

    fun setContext(ctx: IncomingRequests) {
        this.ctx = ctx
    }

    fun getRequests() {
        try {

            val retrofit = Retrofit.Builder()
                .baseUrl(constants.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val retrofitBuilder = retrofit.create(APIRequest::class.java)

            val token = "Bearer " + constants.bearerToken
            val retrofitData = retrofitBuilder.getRequests(token)
            Log.e("fetching invitations", "going...")

            retrofitData.enqueue(object : Callback<List<RequestDataInterface.friendRequests>> {
                override fun onResponse(call: Call<List<RequestDataInterface.friendRequests>>, response: Response<List<RequestDataInterface.friendRequests>>) {
                    Log.e("fetching invitations:", "retrieving body")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.e("fetching invitations: ",
                            (responseBody ?: "Response body is null").toString()
                        )
                        // Parse the responseBody as needed or handle the string response
                        if (responseBody != null) {
                            invitationList = responseBody
                            Log.e("fetching", "invitation list initialized")
                            IncomingRequests.applyChanges(invitationList)
                        }
                    }
                    else {
                        try {
                            var errorBody = response.errorBody()?.string()
                            errorBody =  errorBody!!.substringAfter(":").trim()
                            errorBody = errorBody.replace(Regex("[\"{}]"), "").trim()
                            Log.e("fetching error: ", "HTTP ${response.code()}: $errorBody")

                        } catch (e: Exception) {
                            Log.e("fetching error: ", "Error parsing error response.")
                        }
                    }
                }

                override fun onFailure(call: Call<List<RequestDataInterface.friendRequests>>, t: Throwable) {
                    Log.e("fetching error: ", t.toString())
                }
            })
        }
        catch (e: Exception) {
            Log.e("error", e.toString())
            // Handle the exception here (e.g. log it or display an error message)
        }
    }
}