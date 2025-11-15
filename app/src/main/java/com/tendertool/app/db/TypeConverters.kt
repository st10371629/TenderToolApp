package com.tendertool.app.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tendertool.app.models.SupportingDoc
import com.tendertool.app.models.Tag

class TypeConverters {

    private val gson = Gson()

    // Tag List Converters
    @TypeConverter
    fun fromTagList(tags: List<Tag>?): String? {
        return gson.toJson(tags)
    }

    @TypeConverter
    fun toTagList(tagsString: String?): List<Tag>? {
        val listType = object : TypeToken<List<Tag>>() {}.type
        return gson.fromJson(tagsString, listType)
    }

    // SupportingDoc List Converters
    @TypeConverter
    fun fromSupportingDocList(docs: List<SupportingDoc>?): String? {
        return gson.toJson(docs)
    }

    @TypeConverter
    fun toSupportingDocList(docsString: String?): List<SupportingDoc>? {
        val listType = object : TypeToken<List<SupportingDoc>>() {}.type
        return gson.fromJson(docsString, listType)
    }
}