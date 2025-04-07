package com.example.randomstringgenerator.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.iav.contestdataprovider.Api.Model.RandomText
import com.iav.contestdataprovider.Api.Model.RandomTextResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class RandomStringRepository(private val context: Context) {
    @RequiresApi(Build.VERSION_CODES.O)
    open suspend fun queryRandomString(maxLength: Int): RandomText? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse("content://com.iav.contestdataprovider/text")
                val args = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, maxLength)
                }
                val cursor = context.contentResolver.query(uri, null, args, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        Log.e("RandomStringRepo", "Cursor is null, provider might not be available.")
                        val dataColumnIndex = it.getColumnIndex("data")
                        if (dataColumnIndex != -1) {
                            val json = it.getString(dataColumnIndex)
                            val response = Gson().fromJson(json, RandomTextResponse::class.java)
                            return@withContext response.randomText
                        }
                    }
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}
