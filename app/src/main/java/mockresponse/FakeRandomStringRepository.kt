package mockresponse


import com.example.randomstringgenerator.data.RandomStringRepository
import com.iav.contestdataprovider.Api.Model.RandomText

class FakeRandomStringRepository(private val fakeResult: RandomText?) : RandomStringRepository(null) {

    override suspend fun queryRandomString(maxLength: Int): RandomText? {
        return fakeResult
    }
}
