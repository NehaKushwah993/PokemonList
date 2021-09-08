package com.nehak.pokemonlist.ui

import com.nehak.pokemonlist.backend.dataSource.PokemonRemoteDataSource
import com.nehak.pokemonlist.backend.database.PokemonDao
import com.nehak.pokemonlist.backend.network.PokemonService
import com.nehak.pokemonlist.backend.other.ApiResult
import com.nehak.pokemonlist.backend.repository.PokemonRepository
import com.nehak.pokemonlist.util.MockUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * Created by Neha Kushwah on 8/9/21.
 * PokemonListViewModelTest to test its methods
 */
class PokemonListViewModelTest {

    private lateinit var viewModel: PokemonListViewModel;
    private lateinit var pokemonRepository: PokemonRepository
    private var pokemonDao: PokemonDao = mock(PokemonDao::class.java)
    private var pokemonService: PokemonService = Mockito.mock(PokemonService::class.java)

    @Before
    fun setup() {
        pokemonRepository = PokemonRepository(PokemonRemoteDataSource(pokemonService), pokemonDao)
    }

    @Test
    fun `test fetchPokemonList with 5 elements`() {
        runBlocking {
            val limit = 5;
            val mockData = MockUtil.mockPokemonList(limit)
            `when`(pokemonDao.getPokemonList()).thenReturn(mockData)

            val fetchedDataFlow = pokemonRepository.fetchPokemonList(
                limit = limit,
                onStart = {},
                onComplete = {},
                onError = {}
            ).first();

            Assert.assertNotNull("fetchedDataFlow = ", fetchedDataFlow)
            Assert.assertEquals(fetchedDataFlow, MockUtil.mockPokemonList(limit))
            Assert.assertEquals(fetchedDataFlow[0].name, "Pokemon Name")
        }
    }

    @Test
    fun `test fetchPokemonList with 0 elements in db and service`() {
        runBlocking {
            val limit = 10;
            val mockData = MockUtil.mockPokemonList(0)
            `when`(pokemonDao.getPokemonList()).thenReturn(mockData)
            Mockito.`when`(pokemonService.fetchPokemonList(10))
                .thenReturn(ApiResult.error("Error message", null))

            val fetchedDataFlow = pokemonRepository.fetchPokemonList(
                limit = limit,
                onStart = {},
                onComplete = {},
                onError = {}
            ).toList()

            Assert.assertNotNull("fetchedDataFlow = ", fetchedDataFlow)
            Assert.assertEquals(fetchedDataFlow.size, 0)
            Assert.assertNotEquals(fetchedDataFlow.size, 1)

        }

    }
}