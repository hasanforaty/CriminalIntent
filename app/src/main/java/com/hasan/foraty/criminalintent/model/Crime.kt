package com.hasan.foraty.criminalintent.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(@PrimaryKey val id:UUID =UUID.randomUUID(),
                 var date:Date =Date(),
                 var title:String="",
                 var isSolved:Boolean = false,
                var suspect:String="") {
                    val photoFileName
                        get() = "IMG_$id.jpg"
                }



