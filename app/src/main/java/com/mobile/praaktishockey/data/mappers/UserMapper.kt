package com.mobile.praaktishockey.data.mappers

import com.mobile.praaktishockey.data.entities.UserData
import com.mobile.praaktishockey.domain.entities.UserDTO

class UserMapper : Mapper<UserDTO, UserData> () {

    override fun mapFrom(from: UserDTO): UserData {
        return UserData(id = from.id!!)
    }

}