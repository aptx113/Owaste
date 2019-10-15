package com.epoch.owaste

import androidx.lifecycle.ViewModelProviders
import com.epoch.owaste.card.RewardCardFragment
import com.epoch.owaste.card.RewardCardViewModel
import com.epoch.owaste.maps.MapsViewModel
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MockitoTest {

//    @Mock
//    val viewModel = MapsViewModel()
//
    @Test
    fun get_Exp_Sum() {

        // arrange
        val mockList = listOf(20, 30, 40)
        val expect = 90
        val rewardCard = RewardCardFragment()

        // act
        val actual = rewardCard.getExpSumTest(mockList)

        // assert
        assertEquals(expect, actual)
        

//        `when`(viewModel.getExpSumTest())
//            .thenReturn(mockList.sum())
//
//        Mockito.verify(viewModel).getExpSumTest()
    }
}