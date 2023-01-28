package playground.arrow.core


class City

class Speaker {
    suspend fun nextTalk(): Talk = TODO()
}

class Talk {
    suspend fun getConference(): Conference = TODO()
}

class Conference {
    suspend fun getCity(): City = TODO()
}

suspend fun nextTalkCity(speaker: Speaker): City {
    val talk = speaker.nextTalk()
    val conf = talk.getConference()
    val city = conf.getCity()
    return city
}

suspend fun nextTalkCity2(speaker: Speaker): City {
    return speaker
        .nextTalk()
        .getConference()
        .getCity()
}














