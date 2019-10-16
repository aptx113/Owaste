package com.epoch.owaste

import com.epoch.owaste.card.RewardCardFragment
import junit.framework.Assert
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun get_Exp_Sum() {

        // arrange
        val mockList = listOf(20, 30, 40)
        val expect = 90
        val rewardCard = RewardCardFragment()

        // act
        val actual = rewardCard.getExpSumTest(mockList)

        // assert
        Assert.assertEquals(expect, actual)
    }
}
