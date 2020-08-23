package com.mobile.gympraaktis.data.mappers

import com.mobile.gympraaktis.data.entities.UserData
import com.mobile.gympraaktis.domain.entities.UserDTO

class UserMapper : Mapper<UserDTO, UserData> () {

    override fun mapFrom(from: UserDTO): UserData {
        return UserData(id = from.id!!)
    }

}